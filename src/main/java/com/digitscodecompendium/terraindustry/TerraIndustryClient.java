package com.digitscodecompendium.terraindustry;

import com.digitscodecompendium.terraindustry.client.RefineryControllerScreen;
import com.digitscodecompendium.terraindustry.client.RefineryPortScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Client-only registration for refinery menu screens. */
@Mod(TerraIndustry.MODID)
public final class TerraIndustryClient {
    public TerraIndustryClient(IEventBus modBus) {
        modBus.addListener(this::registerMenuScreens);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.REFINERY_PORT.get(), RefineryPortScreen::new);
        event.register(ModMenus.REFINERY_CONTROLLER.get(), RefineryControllerScreen::new);
    }
}
