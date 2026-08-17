/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.Keyed;
import de.bluecolored.bluemap.core.util.Registry;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.crystalix.activation.CrystalixRuntime;

/** BlueMap 5.22 internal ABI registration boundary. */
public final class BlueMap522Adapter {

    private static final CrystalixRuntime RUNTIME = CrystalixRuntime.INSTANCE;
    private static final BlockRendererType RENDERER = new BlockRendererType.Impl(
            Key.parse("bluemap_crystalix:glass"),
            (pack, gallery, settings) ->
                    new CrystalixRenderer(pack, gallery, settings, RUNTIME)
    );
    private static final ResourcePack.Extension<CrystalixResourceExtension> EXTENSION =
            new CrystalixResourceExtensionType(RUNTIME);
    private static final BlockEntityType GLASS = new BlockEntityType.Impl(
            Key.parse("crystalix:glass_tile"), CrystalixGlassBlockEntityData.class
    );

    private BlueMap522Adapter() {
    }

    public static synchronized boolean install() {
        if (!canRegister(BlockRendererType.REGISTRY, RENDERER)
                || !canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)
                || !canRegister(BlockEntityType.REGISTRY, GLASS)) {
            RUNTIME.disable("registry-collision");
            return false;
        }
        boolean installed = register(BlockRendererType.REGISTRY, RENDERER)
                && register(ResourcePack.Extension.REGISTRY, EXTENSION)
                && register(BlockEntityType.REGISTRY, GLASS);
        if (!installed) {
            RUNTIME.disable("registry-collision");
        }
        return installed;
    }

    static BlockRendererType renderer() {
        return RENDERER;
    }

    static CrystalixResourceExtension extension(ResourcePack resourcePack) {
        return resourcePack.getExtension(EXTENSION);
    }

    private static <T extends Keyed> boolean canRegister(Registry<T> registry, T candidate) {
        T existing = registry.get(candidate.getKey());
        return existing == null || existing == candidate;
    }

    private static <T extends Keyed> boolean register(Registry<T> registry, T candidate) {
        T existing = registry.get(candidate.getKey());
        if (existing == null) {
            registry.register(candidate);
            existing = registry.get(candidate.getKey());
        }
        return existing == candidate;
    }
}
