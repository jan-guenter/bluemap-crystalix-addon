/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.crystalix.activation.CrystalixRuntime;
import io.github.janguenter.bluemap.crystalix.profile.Crystalix300Fusion1312Profile;
import io.github.janguenter.bluemap.crystalix.profile.ExactArtifactDetector;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Exact artifact activation, FULL-sheet slicing, and three-ID routing. */
final class CrystalixResourceExtension implements ResourcePackExtension {

    private static final int FULL_COLUMNS = 8;
    private static final int FULL_LOGICAL_ROWS = 6;
    private static final int FULL_PHYSICAL_ROWS = 8;
    private static final Key SYNTHETIC = Key.parse("bluemap_crystalix:glass");

    private final ResourcePack resourcePack;
    private final CrystalixRuntime runtime;
    private Map<TileKey, Key> tileKeys = Map.of();

    CrystalixResourceExtension(ResourcePack resourcePack, CrystalixRuntime runtime) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.crystalix.disabled")) {
            runtime.route().inactive("operator-disabled");
            return;
        }
        if (!ExactArtifactDetector.matchesPair(roots)) {
            runtime.route().inactive("exact-artifact-pair-missing");
            return;
        }
        runtime.route().activate();
    }

    @Override
    public Set<Key> collectUsedTextureKeys() {
        if (!runtime.route().isActive()) {
            return Set.of();
        }
        LinkedHashSet<Key> used = new LinkedHashSet<>(
                Crystalix300Fusion1312Profile.sourceTextures()
        );
        used.addAll(plannedTiles().values());
        return Set.copyOf(used);
    }

    @Override
    public void bake() {
        if (!runtime.route().isActive()) {
            return;
        }
        try {
            if (!validDispatch(resourcePack.getBlockStates().get(SYNTHETIC))) {
                runtime.route().inactive("synthetic-dispatch-invalid");
                return;
            }
            validatePlainTextures();
            Map<TileKey, Key> planned = plannedTiles();
            for (Key output : planned.values()) {
                if (resourcePack.getTextures().get(output) != null) {
                    runtime.route().inactive("synthetic-texture-collision");
                    return;
                }
            }
            Map<Key, Texture> generated = cropTiles(planned);
            generated.forEach(resourcePack.getTextures()::put);
            tileKeys = Map.copyOf(planned);
            System.out.println("BlueMap Crystalix add-on active: 3 native glass IDs, "
                    + "persisted RGB, and Fusion FULL connectivity.");
        } catch (IOException | RuntimeException exception) {
            runtime.route().inactive("required-texture-invalid");
        }
    }

    @Override
    public Key getBlockStateKey(Key key) {
        return runtime.route().isActive()
                && Crystalix300Fusion1312Profile.BLOCK_IDS.contains(key.getFormatted())
                ? SYNTHETIC : key;
    }

    @Override
    public void getBlockProperties(BlockState state, BlockProperties.Builder builder) {
        if (runtime.route().isActive()
                && Crystalix300Fusion1312Profile.BLOCK_IDS.contains(
                        state.getId().getFormatted()
                )) {
            builder.culling(false).occluding(false).cullingIdentical(false);
        }
    }

    Key tile(Key source, int index) {
        return tileKeys.get(new TileKey(source, index));
    }

    private void validatePlainTextures() throws IOException {
        for (Key source : Crystalix300Fusion1312Profile.sourceTextures()) {
            Texture texture = resourcePack.getTextures().get(source);
            if (texture == null) {
                throw new IOException("required Crystalix texture missing");
            }
            BufferedImage image = texture.getTextureImage();
            boolean full = Crystalix300Fusion1312Profile.fullSheets().contains(source);
            int expected = full ? 128 : 16;
            if (image.getWidth() != expected || image.getHeight() != expected) {
                throw new IOException("Crystalix texture dimensions changed");
            }
        }
    }

    private Map<TileKey, Key> plannedTiles() {
        Map<TileKey, Key> planned = new LinkedHashMap<>();
        for (Key source : Crystalix300Fusion1312Profile.fullSheets()) {
            for (int index = 0; index < FULL_COLUMNS * FULL_LOGICAL_ROWS; index++) {
                Key output = Key.parse("bluemap_crystalix:tiles/"
                        + source.getNamespace() + "/" + source.getValue() + "/" + index);
                planned.put(new TileKey(source, index), output);
            }
        }
        return planned;
    }

    private Map<Key, Texture> cropTiles(Map<TileKey, Key> planned) throws IOException {
        Map<Key, Texture> generated = new LinkedHashMap<>();
        for (Map.Entry<TileKey, Key> request : planned.entrySet()) {
            Texture source = resourcePack.getTextures().get(request.getKey().source());
            if (source == null) {
                throw new IOException("required FULL sheet is missing");
            }
            BufferedImage sheet = source.getTextureImage();
            if (sheet.getWidth() != 128 || sheet.getHeight() != 128) {
                throw new IOException("FULL sheet dimensions changed");
            }
            int tileWidth = sheet.getWidth() / FULL_COLUMNS;
            int tileHeight = sheet.getHeight() / FULL_PHYSICAL_ROWS;
            int index = request.getKey().index();
            int x = index % FULL_COLUMNS * tileWidth;
            int y = index / FULL_COLUMNS * tileHeight;
            BufferedImage copy = new BufferedImage(
                    tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D graphics = copy.createGraphics();
            try {
                graphics.drawImage(sheet, -x, -y, null);
            } finally {
                graphics.dispose();
            }
            generated.put(request.getValue(), Texture.from(request.getValue(), copy));
        }
        if (generated.size() != planned.size()) {
            throw new IOException("FULL tile output is incomplete");
        }
        return generated;
    }

    private static boolean validDispatch(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state
    ) {
        if (state == null || state.getMultipart() != null) {
            return false;
        }
        Variants variants = state.getVariants();
        if (variants == null || variants.getDefaultVariant() == null) {
            return false;
        }
        VariantSet set = variants.getDefaultVariant();
        if (set.getVariants().length != 1) {
            return false;
        }
        Variant variant = set.getVariants()[0];
        return variant.getRenderer() == BlueMap522Adapter.renderer()
                && ResourcePack.MISSING_BLOCK_MODEL.equals(variant.getModel())
                && !variant.isTransformed() && !variant.isUvlock();
    }

    private record TileKey(Key source, int index) {
    }
}
