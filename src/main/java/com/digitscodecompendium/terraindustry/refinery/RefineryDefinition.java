package com.digitscodecompendium.terraindustry.refinery;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

/** Immutable configuration for one refinery block. All schedule times are UTC. */
public record RefineryDefinition(
        ResourceLocation id,
        @Nullable Instant startsAt,
        List<DailyWindow> activeWindows,
        int cycleTicks,
        List<CatalystTransformationRecipe> catalystRecipes,
        @Nullable RefineryOperatingRate fuel,
        @Nullable RefineryOperatingRate coolant,
        List<CatalystCrystallizationRecipe> crystallizationRecipes
) {
    public RefineryDefinition(ResourceLocation id, @Nullable Instant startsAt, List<DailyWindow> activeWindows) {
        this(id, startsAt, activeWindows, 20, List.of(), null, null, List.of());
    }

    public RefineryDefinition {
        Objects.requireNonNull(id, "id");
        activeWindows = List.copyOf(activeWindows);
        catalystRecipes = List.copyOf(catalystRecipes);
        crystallizationRecipes = List.copyOf(crystallizationRecipes);
        if (cycleTicks < 1) throw new IllegalArgumentException("cycleTicks must be at least one");
    }

    public boolean isScheduledNow(Instant now) {
        if (startsAt != null && now.isBefore(startsAt)) return false;
        LocalTime utcTime = now.atZone(ZoneOffset.UTC).toLocalTime();
        return activeWindows.stream().anyMatch(window -> window.contains(utcTime));
    }

    /** A start equal to end means an all-day active window. A later end-to-start window crosses midnight. */
    public record DailyWindow(LocalTime start, LocalTime end) {
        public DailyWindow {
            Objects.requireNonNull(start, "start");
            Objects.requireNonNull(end, "end");
        }

        public boolean contains(LocalTime time) {
            if (start.equals(end)) return true;
            return start.isBefore(end) ? !time.isBefore(start) && time.isBefore(end)
                    : !time.isBefore(start) || time.isBefore(end);
        }
    }
}
