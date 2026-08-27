package com.digitscodecompendium.terraindustry.refinery;

import net.minecraft.resources.ResourceLocation;
import java.util.Objects;

/** A quantity of one supported refinery medium. Energy has no registry id. */
public record RefineryResource(Kind kind, ResourceLocation id, int amount) {
    public enum Kind { ITEM, FLUID, ENERGY }
    public RefineryResource {
        Objects.requireNonNull(kind, "kind");
        if (kind == Kind.ENERGY && id != null) throw new IllegalArgumentException("Energy cannot have an id");
        if (kind != Kind.ENERGY && id == null) throw new IllegalArgumentException(kind + " needs an id");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
    }
    public static RefineryResource item(String id, int amount) { return new RefineryResource(Kind.ITEM, ResourceLocation.parse(id), amount); }
    public static RefineryResource fluid(String id, int amount) { return new RefineryResource(Kind.FLUID, ResourceLocation.parse(id), amount); }
    public static RefineryResource energy(int amount) { return new RefineryResource(Kind.ENERGY, null, amount); }
}
