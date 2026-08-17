/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.model;

import de.bluecolored.bluemap.core.util.Direction;

/** Unrotated Minecraft block-model texture axes for one cube face. */
public record TextureFrame(Direction face, AxisVector up, AxisVector right) {

    public static TextureFrame forFace(Direction face) {
        AxisVector up = switch (face) {
            case DOWN -> new AxisVector(0, 0, 1);
            case UP -> new AxisVector(0, 0, -1);
            case NORTH, SOUTH, WEST, EAST -> new AxisVector(0, 1, 0);
        };
        AxisVector right = switch (face) {
            case DOWN, UP, SOUTH -> new AxisVector(1, 0, 0);
            case NORTH -> new AxisVector(-1, 0, 0);
            case WEST -> new AxisVector(0, 0, 1);
            case EAST -> new AxisVector(0, 0, -1);
        };
        return new TextureFrame(face, up, right);
    }

    public AxisVector offset(FusionDirection direction) {
        return switch (direction) {
            case TOP -> up;
            case TOP_RIGHT -> up.add(right);
            case RIGHT -> right;
            case BOTTOM_RIGHT -> right.subtract(up);
            case BOTTOM -> up.negate();
            case BOTTOM_LEFT -> up.negate().subtract(right);
            case LEFT -> right.negate();
            case TOP_LEFT -> up.subtract(right);
        };
    }
}
