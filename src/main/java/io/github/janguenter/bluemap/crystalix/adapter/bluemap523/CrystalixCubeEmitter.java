/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.block.ExtendedBlock;
import io.github.janguenter.bluemap.crystalix.model.AxisVector;
import io.github.janguenter.bluemap.crystalix.model.FusionDirection;
import io.github.janguenter.bluemap.crystalix.model.FusionFullTextureSelector;
import io.github.janguenter.bluemap.crystalix.model.TextureFrame;
import io.github.janguenter.bluemap.crystalix.profile.Crystalix300Fusion1312Profile;

import java.util.ArrayList;
import java.util.List;

/** Emits a tinted cube with Crystalix's exact native-glass connection rules. */
final class CrystalixCubeEmitter {

    private final ResourcePack resourcePack;
    private final TextureGallery textureGallery;
    private final RenderSettings renderSettings;
    private final CrystalixResourceExtension extension;

    CrystalixCubeEmitter(
            ResourcePack resourcePack,
            TextureGallery textureGallery,
            RenderSettings renderSettings
    ) {
        this.resourcePack = resourcePack;
        this.textureGallery = textureGallery;
        this.renderSettings = renderSettings;
        this.extension = BlueMap523Adapter.extension(resourcePack);
    }

    boolean render(
            BlockNeighborhood block,
            CrystalixGlassBlockEntityData data,
            TileModelView target,
            Color mapColor
    ) {
        BlockState state = block.getBlockState();
        boolean transparent = "true".equals(state.getProperties().get("transparent"));
        Crystalix300Fusion1312Profile.TexturePair pair =
                Crystalix300Fusion1312Profile.textures(state.getId().getFormatted());
        if (pair == null) {
            return false;
        }
        Key source = pair.select(transparent);
        List<FacePlan> plans = new ArrayList<>(6);
        for (Direction face : Direction.values()) {
            if (renderSettings.isRenderTopOnly() && face != Direction.UP) {
                continue;
            }
            if (culled(block, face)) {
                continue;
            }
            Key texture = source;
            if (pair.fullSheet()) {
                int tile = FusionFullTextureSelector.tile(
                        connections(block, TextureFrame.forFace(face), transparent)
                );
                texture = extension.tile(source, tile);
            }
            Texture resolved = texture == null ? null : resourcePack.getTextures().get(texture);
            if (resolved == null) {
                return false;
            }
            plans.add(new FacePlan(face, texture, resolved, light(block, face, state)));
        }

        int rgb = data.rgb();
        for (FacePlan plan : plans) {
            emitFace(target, plan, rgb);
            if (plan.face() == Direction.UP) {
                setMapColor(mapColor, plan, rgb);
            }
        }
        return true;
    }

    private static int connections(
            BlockNeighborhood block,
            TextureFrame frame,
            boolean transparent
    ) {
        int mask = 0;
        String ownId = block.getBlockState().getId().getFormatted();
        for (FusionDirection direction : FusionDirection.values()) {
            AxisVector offset = frame.offset(direction);
            BlockState neighbor = block.getNeighborBlock(
                    offset.x(), offset.y(), offset.z()
            ).getBlockState();
            if (ownId.equals(neighbor.getId().getFormatted())
                    && "false".equals(neighbor.getProperties().get("invisible"))
                    && Boolean.toString(transparent).equals(
                            neighbor.getProperties().get("transparent")
                    )) {
                mask |= 1 << direction.bit();
            }
        }
        return mask;
    }

    private static boolean culled(BlockNeighborhood block, Direction face) {
        AxisVector axis = normal(face);
        ExtendedBlock neighbor = block.getNeighborBlock(axis.x(), axis.y(), axis.z());
        BlockProperties neighborProperties = neighbor.getProperties();
        BlockState own = block.getBlockState();
        BlockState neighborState = neighbor.getBlockState();
        return neighborProperties.isCulling()
                || own.getId().equals(neighborState.getId())
                && "false".equals(neighborState.getProperties().get("invisible"));
    }

    private static LightSample light(
            BlockNeighborhood block,
            Direction face,
            BlockState state
    ) {
        AxisVector axis = normal(face);
        LightData own = block.getLightData();
        LightData faced = block.getNeighborBlock(axis.x(), axis.y(), axis.z()).getLightData();
        String light = state.getProperties().get("light");
        int emission = "light".equals(light) || "fake_light".equals(light) ? 15 : 0;
        return new LightSample(
                Math.max(own.getSkyLight(), faced.getSkyLight()),
                Math.max(emission, Math.max(own.getBlockLight(), faced.getBlockLight()))
        );
    }

    private void emitFace(TileModelView target, FacePlan plan, int rgb) {
        float[][] vertices = vertices(plan.face());
        int first = target.add(2);
        TileModel model = target.getTileModel();
        model.setPositions(first,
                vertices[0][0], vertices[0][1], vertices[0][2],
                vertices[1][0], vertices[1][1], vertices[1][2],
                vertices[2][0], vertices[2][1], vertices[2][2]);
        model.setPositions(first + 1,
                vertices[0][0], vertices[0][1], vertices[0][2],
                vertices[2][0], vertices[2][1], vertices[2][2],
                vertices[3][0], vertices[3][1], vertices[3][2]);
        // The selected output is one cropped 16x16 tile, so it must fill the face.
        model.setUvs(first, 0F, 1F, 1F, 1F, 1F, 0F);
        model.setUvs(first + 1, 0F, 1F, 1F, 0F, 0F, 0F);
        int material = textureGallery.get(plan.textureKey());
        model.setMaterialIndex(first, material);
        model.setMaterialIndex(first + 1, material);
        float red = (rgb >>> 16 & 0xff) / 255F;
        float green = (rgb >>> 8 & 0xff) / 255F;
        float blue = (rgb & 0xff) / 255F;
        model.setColor(first, red, green, blue);
        model.setColor(first + 1, red, green, blue);
        model.setAOs(first, 1F, 1F, 1F);
        model.setAOs(first + 1, 1F, 1F, 1F);
        model.setSunlight(first, plan.light().sunlight());
        model.setSunlight(first + 1, plan.light().sunlight());
        model.setBlocklight(first, plan.light().blocklight());
        model.setBlocklight(first + 1, plan.light().blocklight());
    }

    private void setMapColor(Color mapColor, FacePlan plan, int rgb) {
        Color sample = new Color().set(plan.texture().getColorPremultiplied());
        Color tint = new Color().set(0xff00_0000 | rgb);
        sample.multiply(tint).premultiplied();
        float light = Math.max(plan.light().sunlight(), plan.light().blocklight()) / 15F;
        light = (1F - renderSettings.getAmbientLight()) * light
                + renderSettings.getAmbientLight();
        sample.r *= light;
        sample.g *= light;
        sample.b *= light;
        float opacity = plan.texture().getColorPremultiplied().a;
        if (sample.a > 0F) {
            sample.flatten().straight();
            sample.a = opacity;
        }
        mapColor.set(sample);
    }

    private static AxisVector normal(Direction face) {
        return switch (face) {
            case DOWN -> new AxisVector(0, -1, 0);
            case UP -> new AxisVector(0, 1, 0);
            case NORTH -> new AxisVector(0, 0, -1);
            case SOUTH -> new AxisVector(0, 0, 1);
            case WEST -> new AxisVector(-1, 0, 0);
            case EAST -> new AxisVector(1, 0, 0);
        };
    }

    private static float[][] vertices(Direction face) {
        return switch (face) {
            case DOWN -> new float[][]{
                {0F, 0F, 0F}, {1F, 0F, 0F}, {1F, 0F, 1F}, {0F, 0F, 1F}
            };
            case UP -> new float[][]{
                {0F, 1F, 1F}, {1F, 1F, 1F}, {1F, 1F, 0F}, {0F, 1F, 0F}
            };
            case NORTH -> new float[][]{
                {1F, 0F, 0F}, {0F, 0F, 0F}, {0F, 1F, 0F}, {1F, 1F, 0F}
            };
            case SOUTH -> new float[][]{
                {0F, 0F, 1F}, {1F, 0F, 1F}, {1F, 1F, 1F}, {0F, 1F, 1F}
            };
            case WEST -> new float[][]{
                {0F, 0F, 0F}, {0F, 0F, 1F}, {0F, 1F, 1F}, {0F, 1F, 0F}
            };
            case EAST -> new float[][]{
                {1F, 0F, 1F}, {1F, 0F, 0F}, {1F, 1F, 0F}, {1F, 1F, 1F}
            };
        };
    }

    private record LightSample(int sunlight, int blocklight) {
    }

    private record FacePlan(
            Direction face,
            Key textureKey,
            Texture texture,
            LightSample light
    ) {
    }
}
