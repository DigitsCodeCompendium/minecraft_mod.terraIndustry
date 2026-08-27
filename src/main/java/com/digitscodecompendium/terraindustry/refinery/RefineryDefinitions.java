package com.digitscodecompendium.terraindustry.refinery;

import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Arrays;

/** Public Java bridge intended for KubeJS startup scripts; no KubeJS dependency is required. */
public final class RefineryDefinitions {
    private static final Map<ResourceLocation, RefineryDefinition> DEFINITIONS = new ConcurrentHashMap<>();
    private static volatile ResourceLocation defaultDefinition;
    public static void register(RefineryDefinition definition) { DEFINITIONS.put(definition.id(), definition); }

    /**
     * KubeJS-friendly registration method. {@code startsAt} is an ISO-8601 UTC instant or null;
     * each period is {@code HH:mm-HH:mm} UTC. Use {@code 00:00-00:00} for all day.
     */
    public static void register(String id, String startsAt, String[] periods) {
        ResourceLocation key = ResourceLocation.parse(id);
        Instant start = startsAt == null || startsAt.isBlank() ? null : Instant.parse(startsAt);
        var windows = Arrays.stream(periods).map(RefineryDefinitions::parsePeriod).toList();
        register(new RefineryDefinition(key, start, windows));
    }

    /** Registers a catalyst refinery and its independent operating streams. */
    public static void register(String id, String startsAt, String[] periods, int cycleTicks,
                                CatalystTransformationRecipe[] catalystRecipes,
                                RefineryOperatingRate fuel, RefineryOperatingRate coolant,
                                CatalystCrystallizationRecipe[] crystallizationRecipes) {
        ResourceLocation key = ResourceLocation.parse(id);
        Instant start = startsAt == null || startsAt.isBlank() ? null : Instant.parse(startsAt);
        var windows = Arrays.stream(periods).map(RefineryDefinitions::parsePeriod).toList();
        register(new RefineryDefinition(key, start, windows, cycleTicks, Arrays.asList(catalystRecipes), fuel, coolant,
                Arrays.asList(crystallizationRecipes)));
    }

    private static RefineryDefinition.DailyWindow parsePeriod(String period) {
        String[] parts = period.split("-", -1);
        if (parts.length != 2) throw new IllegalArgumentException("Invalid refinery UTC period: " + period);
        return new RefineryDefinition.DailyWindow(LocalTime.parse(parts[0]), LocalTime.parse(parts[1]));
    }
    public static Optional<RefineryDefinition> find(ResourceLocation id) { return Optional.ofNullable(DEFINITIONS.get(id)); }
    /** Assigns the definition automatically selected by newly placed/unconfigured controllers. */
    public static void setDefault(String id) { defaultDefinition = ResourceLocation.parse(id); }
    public static Optional<ResourceLocation> defaultId() { return Optional.ofNullable(defaultDefinition); }
    public static void clear() { DEFINITIONS.clear(); }
    private RefineryDefinitions() { }
}
