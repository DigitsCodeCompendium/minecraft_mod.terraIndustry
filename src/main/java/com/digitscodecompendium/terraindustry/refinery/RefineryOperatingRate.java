package com.digitscodecompendium.terraindustry.refinery;

import java.util.Objects;

/** A fuel or coolant stream owned by a refinery, independent of catalyst transformations. */
public record RefineryOperatingRate(RefineryResource resource, int intervalTicks) {
    public RefineryOperatingRate {
        Objects.requireNonNull(resource, "resource");
        if (intervalTicks < 1) throw new IllegalArgumentException("intervalTicks must be at least one");
    }
    public static RefineryOperatingRate everyTicks(RefineryResource resource, int intervalTicks) {
        return new RefineryOperatingRate(resource, intervalTicks);
    }
}
