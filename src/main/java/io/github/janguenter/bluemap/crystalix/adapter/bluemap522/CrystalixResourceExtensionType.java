/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.crystalix.activation.CrystalixRuntime;

/** Resource-pack extension factory registered before resource loading. */
final class CrystalixResourceExtensionType
        implements ResourcePack.Extension<CrystalixResourceExtension> {

    private static final Key KEY = Key.parse("bluemap_crystalix:prototype");

    private final CrystalixRuntime runtime;

    CrystalixResourceExtensionType(CrystalixRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public Key getKey() {
        return KEY;
    }

    @Override
    public CrystalixResourceExtension create(ResourcePack pack) {
        return new CrystalixResourceExtension(pack, runtime);
    }
}
