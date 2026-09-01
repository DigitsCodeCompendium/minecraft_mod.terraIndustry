package com.digitscodecompendium.terraindustry;

import com.digitscodecompendium.terraindustry.client.RefineryControllerScreen;
import com.digitscodecompendium.terraindustry.client.RefineryPortScreen;
import com.digitscodecompendium.terraindustry.client.EffectsBlockScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Client-only registration for refinery menu screens. */
@Mod(value = TerraIndustry.MODID, dist = Dist.CLIENT)
public final class TerraIndustryClient {
    public TerraIndustryClient(IEventBus modBus) {
        modBus.addListener(this::registerMenuScreens);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.REFINERY_PORT.get(), RefineryPortScreen::new);
        event.register(ModMenus.REFINERY_CONTROLLER.get(), RefineryControllerScreen::new);
        event.register(ModMenus.EFFECTS_BLOCK.get(), EffectsBlockScreen::new);
    }

}
