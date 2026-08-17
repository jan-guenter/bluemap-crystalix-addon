/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.profile;

import de.bluecolored.bluemap.core.util.Key;

import java.util.Map;
import java.util.Set;

/** Exact All the Mons 1.2.0 Crystalix/Fusion static-rendering tuple. */
public final class Crystalix300Fusion1312Profile {

    public static final long CRYSTALIX_SIZE = 817_004L;
    public static final String CRYSTALIX_SHA256 =
            "42f97cf776cff8261bf671e64a333bbec65a8bf28e519d39cd958e0af9848e6c";
    public static final long FUSION_SIZE = 923_270L;
    public static final String FUSION_SHA256 =
            "17f5215648a98bcde4134577b013200dbf363273ae282449c51408ae8346f2fa";

    public static final Set<String> BLOCK_IDS = Set.of(
            "crystalix:crystalix_glass",
            "crystalix:clear_crystalix_glass",
            "crystalix:bordered_crystalix_glass"
    );

    private static final Map<String, TexturePair> TEXTURES = Map.of(
            "crystalix:crystalix_glass", new TexturePair(
                    Key.parse("crystalix:block/colored_crystalix_glass"),
                    Key.parse("crystalix:block/crystalix_glass"),
                    false
            ),
            "crystalix:clear_crystalix_glass", new TexturePair(
                    Key.parse("crystalix:block/colored_clear_crystalix_glass"),
                    Key.parse("crystalix:block/clear_crystalix_glass"),
                    true
            ),
            "crystalix:bordered_crystalix_glass", new TexturePair(
                    Key.parse("crystalix:block/colored_bordered_crystalix_glass"),
                    Key.parse("crystalix:block/bordered_crystalix_glass"),
                    true
            )
    );

    private Crystalix300Fusion1312Profile() {
    }

    public static TexturePair textures(String blockId) {
        return TEXTURES.get(blockId);
    }

    public static Set<Key> sourceTextures() {
        java.util.LinkedHashSet<Key> keys = new java.util.LinkedHashSet<>();
        TEXTURES.values().forEach(pair -> {
            keys.add(pair.colored());
            keys.add(pair.transparent());
        });
        return Set.copyOf(keys);
    }

    public static Set<Key> fullSheets() {
        java.util.LinkedHashSet<Key> keys = new java.util.LinkedHashSet<>();
        TEXTURES.values().stream().filter(TexturePair::fullSheet).forEach(pair -> {
            keys.add(pair.colored());
            keys.add(pair.transparent());
        });
        return Set.copyOf(keys);
    }

    public record TexturePair(Key colored, Key transparent, boolean fullSheet) {

        public Key select(boolean transparentState) {
            return transparentState ? transparent : colored;
        }
    }
}
