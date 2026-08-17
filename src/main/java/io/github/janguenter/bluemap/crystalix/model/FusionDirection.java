/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.model;

/** Stable bit order used by Fusion's FULL connected-texture sheet. */
public enum FusionDirection {
    TOP,
    TOP_RIGHT,
    RIGHT,
    BOTTOM_RIGHT,
    BOTTOM,
    BOTTOM_LEFT,
    LEFT,
    TOP_LEFT;

    public int bit() {
        return ordinal();
    }
}
