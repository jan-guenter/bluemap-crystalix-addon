/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.activation;

/** Process-scoped activation state for Crystalix 3.0.0 plus Fusion 1.3.12. */
public final class CrystalixRuntime {

    public static final CrystalixRuntime INSTANCE = new CrystalixRuntime();

    private final RouteActivation route = new RouteActivation();

    private CrystalixRuntime() {
    }

    public RouteActivation route() {
        return route;
    }

    public void disable(String detail) {
        route.fail(detail);
    }
}
