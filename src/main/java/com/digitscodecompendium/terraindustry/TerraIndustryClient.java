package com.digitscodecompendium.terraindustry;

import com.digitscodecompendium.terraindustry.client.RefineryControllerScreen;
import com.digitscodecompendium.terraindustry.client.RefineryPortScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Client-only registration for refinery menu screens. */
@Mod(value = TerraIndustry.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TerraIndustry.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class TerraIndustryClient {
    @SubscribeEvent
    static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.REFINERY_PORT.get(), RefineryPortScreen::new);
        event.register(ModMenus.REFINERY_CONTROLLER.get(), RefineryControllerScreen::new);
    }
}
