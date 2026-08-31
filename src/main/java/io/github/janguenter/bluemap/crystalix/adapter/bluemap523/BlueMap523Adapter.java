/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.RegistryGuard;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.ResourceExtensionType;
import io.github.janguenter.bluemap.crystalix.activation.CrystalixRuntime;

/** Exact BlueMap 5.23 feature-backport registration boundary. */
public final class BlueMap523Adapter {

    private static final CrystalixRuntime RUNTIME = CrystalixRuntime.INSTANCE;
    private static final Key EXTENSION_KEY = Key.parse("bluemap_crystalix:prototype");
    private static final BlockRendererType RENDERER = new BlockRendererType.Impl(
            Key.parse("bluemap_crystalix:glass"),
            (pack, gallery, settings) ->
                    new CrystalixRenderer(pack, gallery, settings, RUNTIME)
    );
    private static final ResourcePack.Extension<CrystalixResourceExtension> EXTENSION =
            new ResourceExtensionType<>(
                    EXTENSION_KEY,
                    pack -> new CrystalixResourceExtension(pack, RUNTIME)
            );
    private static final BlockEntityType GLASS = new BlockEntityType.Impl(
            Key.parse("crystalix:glass_tile"), CrystalixGlassBlockEntityData.class
    );

    private BlueMap523Adapter() {
    }

    public static synchronized boolean install() {
        if (!RegistryGuard.canRegister(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)
                || !RegistryGuard.canRegister(BlockEntityType.REGISTRY, GLASS)) {
            RUNTIME.disable("registry-collision");
            return false;
        }
        boolean installed = RegistryGuard.register(BlockRendererType.REGISTRY, RENDERER)
                && RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)
                && RegistryGuard.register(BlockEntityType.REGISTRY, GLASS);
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

    static ResourcePack.Extension<CrystalixResourceExtension> extensionType() {
        return EXTENSION;
    }
}
