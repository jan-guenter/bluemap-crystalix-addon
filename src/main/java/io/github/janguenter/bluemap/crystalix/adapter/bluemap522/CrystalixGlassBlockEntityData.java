/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.adapter.bluemap522;

import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;

/** Exact persisted visual field from Crystalix's glass block entity. */
public final class CrystalixGlassBlockEntityData extends MCABlockEntity {

    @NBTName("color")
    private Object color;

    public CrystalixGlassBlockEntityData() {
    }

    boolean hasColor() {
        return color instanceof Integer;
    }

    int rgb() {
        return color instanceof Integer integer ? integer & 0x00ff_ffff : 0x00ff_ffff;
    }
}
