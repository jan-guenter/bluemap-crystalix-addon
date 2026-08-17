/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.crystalix.activation;

import java.util.Locale;
import java.util.Objects;

/** Thread-safe fail-closed state for the exact Crystalix route. */
public final class RouteActivation {

    private volatile Snapshot snapshot = new Snapshot(State.INACTIVE, "not-installed");

    public Snapshot snapshot() {
        return snapshot;
    }

    public boolean isActive() {
        return snapshot.state() == State.ACTIVE;
    }

    public synchronized void activate() {
        if (snapshot.state() != State.FAILED) {
            snapshot = new Snapshot(State.ACTIVE, "exact-profile");
        }
    }

    public synchronized void inactive(String detail) {
        if (snapshot.state() != State.FAILED) {
            snapshot = new Snapshot(State.INACTIVE, normalize(detail));
            System.out.println("BlueMap Crystalix add-on inactive: " + snapshot.detail() + ".");
        }
    }

    public synchronized void fail(String detail) {
        snapshot = new Snapshot(State.FAILED, normalize(detail));
        System.err.println("BlueMap Crystalix add-on failed: " + snapshot.detail() + ".");
    }

    private static String normalize(String value) {
        Objects.requireNonNull(value, "detail");
        String normalized = value.trim().toLowerCase(Locale.ROOT).replace(' ', '-');
        if (!normalized.matches("[a-z0-9][a-z0-9._:-]*")) {
            throw new IllegalArgumentException("detail must be a lowercase wire value");
        }
        return normalized;
    }

    public enum State {
        INACTIVE,
        ACTIVE,
        FAILED
    }

    public record Snapshot(State state, String detail) {

        public Snapshot {
            Objects.requireNonNull(state, "state");
            detail = normalize(detail);
        }
    }
}
