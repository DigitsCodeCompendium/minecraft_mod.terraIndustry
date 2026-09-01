package com.digitscodecompendium.terraindustry;

import com.digitscodecompendium.terraindustry.effects.UpdateEffectsConfigPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Registers the small server-bound packets used by Terra Industry configuration screens. */
public final class ModPayloads {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(UpdateEffectsConfigPayload.TYPE, UpdateEffectsConfigPayload.STREAM_CODEC,
                UpdateEffectsConfigPayload::handle);
    }

    private ModPayloads() {
    }
}
