/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.crystalix.activation.CrystalixRuntime;
import io.github.janguenter.bluemap.crystalix.profile.Crystalix300Fusion1312Profile;

import java.util.Set;

/** Routes exact Crystalix native glass state to the focused cube emitter. */
final class CrystalixRenderer implements BlockRenderer {

    private static final Set<String> BOOLEANS = Set.of("true", "false");
    private static final Set<String> LIGHTS = Set.of(
            "none", "light", "fake_light", "dark", "fake_dark"
    );
    private static final Set<String> GHOSTS = Set.of(
            "block_all", "allow_all", "block_player", "allow_player",
            "block_monster", "allow_monster", "block_animal", "allow_animal",
            "block_adult", "allow_adult"
    );

    private final ResourcePack resourcePack;
    private final CrystalixRuntime runtime;
    private final ResourceModelRenderer stock;
    private final CrystalixCubeEmitter emitter;
    private final BoundedDiagnostics diagnostics = new BoundedDiagnostics();

    CrystalixRenderer(
            ResourcePack resourcePack,
            TextureGallery textureGallery,
            RenderSettings renderSettings,
            CrystalixRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
        this.stock = new ResourceModelRenderer(resourcePack, textureGallery, renderSettings);
        this.emitter = new CrystalixCubeEmitter(
                resourcePack, textureGallery, renderSettings
        );
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant dispatch,
            TileModelView target,
            Color mapColor
    ) {
        int start = target.getStart();
        Color initialMapColor = new Color().set(mapColor);
        BlockState state = block.getBlockState();
        if (!runtime.route().isActive() || !accepts(state)) {
            diagnostics.report("inactive-or-malformed-state");
            renderStock(block, target, mapColor);
            return;
        }
        if ("true".equals(state.getProperties().get("invisible"))) {
            mapColor.set(0F, 0F, 0F, 0F, true);
            return;
        }
        if (!(block.getBlockEntity() instanceof CrystalixGlassBlockEntityData data)
                || !data.hasColor()) {
            diagnostics.report("persisted-color-missing");
            renderStock(block, target, mapColor);
            return;
        }
        try {
            if (!emitter.render(block, data, target, mapColor)) {
                diagnostics.report("resource-render-failed");
                resetAndRenderStock(block, target, start, mapColor, initialMapColor);
            }
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            diagnostics.report("contained-render-failure");
            resetAndRenderStock(block, target, start, mapColor, initialMapColor);
        }
    }

    static boolean accepts(BlockState state) {
        if (state == null || !Crystalix300Fusion1312Profile.BLOCK_IDS.contains(
                state.getId().getFormatted()
        )) {
            return false;
        }
        return BOOLEANS.contains(state.getProperties().get("waterlogged"))
                && BOOLEANS.contains(state.getProperties().get("invisible"))
                && BOOLEANS.contains(state.getProperties().get("shadeless"))
                && BOOLEANS.contains(state.getProperties().get("transparent"))
                && LIGHTS.contains(state.getProperties().get("light"))
                && GHOSTS.contains(state.getProperties().get("ghost"));
    }

    private void resetAndRenderStock(
            BlockNeighborhood block,
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor
    ) {
        target.getTileModel().reset(start);
        target.initialize(start);
        mapColor.set(initialMapColor);
        renderStock(block, target, mapColor);
    }

    private void renderStock(
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState original =
                resourcePack.getBlockStates().get(block.getBlockState().getId());
        if (original == null) {
            return;
        }
        original.forEach(
                block.getBlockState(), block.getX(), block.getY(), block.getZ(),
                variant -> {
                    target.initialize();
                    stock.render(block, variant, target, mapColor);
                }
        );
    }
}
