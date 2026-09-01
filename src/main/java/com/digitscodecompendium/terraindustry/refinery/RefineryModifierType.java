package com.digitscodecompendium.terraindustry.refinery;

import net.minecraft.network.chat.Component;

public enum RefineryModifierType {
    NONE("modifier.terraindustry.none", 0),
    ACCELERATION("item.terraindustry.acceleration_modifier", 3 * 20),
    SABOTAGE("item.terraindustry.sabotage_modifier", 10 * 20),
    CRYSTALLIZATION("item.terraindustry.crystallization_modifier", 6 * 20);

    private final String translationKey;
    private final int activationTicks;

    RefineryModifierType(String translationKey, int activationTicks) {
        this.translationKey = translationKey;
        this.activationTicks = activationTicks;
    }

    public Component displayName() {
        return Component.translatable(translationKey);
    }

    public int activationTicks() {
        return activationTicks;
    }
}
