/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.profile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Set;

/** Bounded exact-byte detector for the current Crystalix and Fusion pair. */
public final class ExactArtifactDetector {

    private static final int MAX_ROOTS = 4_096;
    private static final int BUFFER_SIZE = 64 * 1024;

    private ExactArtifactDetector() {
    }

    public static boolean matchesPair(Iterable<Path> roots) {
        boolean crystalix = false;
        boolean fusion = false;
        Set<Path> inspected = new HashSet<>();
        int count = 0;
        try {
            for (Path root : roots) {
                if (Thread.currentThread().isInterrupted() || ++count > MAX_ROOTS) {
                    return false;
                }
                if (root == null || !Files.isRegularFile(root)) {
                    continue;
                }
                Path real = root.toRealPath();
                if (!inspected.add(real)) {
                    continue;
                }
                long size = Files.size(real);
                if (size == Crystalix300Fusion1312Profile.CRYSTALIX_SIZE) {
                    crystalix |= Crystalix300Fusion1312Profile.CRYSTALIX_SHA256.equals(
                            digest(real)
                    );
                } else if (size == Crystalix300Fusion1312Profile.FUSION_SIZE) {
                    fusion |= Crystalix300Fusion1312Profile.FUSION_SHA256.equals(digest(real));
                }
            }
        } catch (IOException exception) {
            return false;
        }
        return crystalix && fusion;
    }

    private static String digest(Path path) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        try (InputStream input = Files.newInputStream(path)) {
            int read;
            while ((read = input.read(buffer)) >= 0) {
                if (read > 0) {
                    digest.update(buffer, 0, read);
                }
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
