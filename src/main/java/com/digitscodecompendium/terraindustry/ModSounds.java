package com.digitscodecompendium.terraindustry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Sound events provided by Terra Industry. */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, TerraIndustry.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> EFFECTS_BLOCK_PULSE = SOUND_EVENTS.register(
            "effects_block_pulse", () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(TerraIndustry.MODID, "effects_block_pulse")));

    private ModSounds() {
    }
}
