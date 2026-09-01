package com.digitscodecompendium.terraindustry;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import com.digitscodecompendium.terraindustry.refinery.BuiltinRefineries;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TerraIndustry.MODID)
public class TerraIndustry {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "terraindustry";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public TerraIndustry(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModBlocks.ITEMS.register(modBus);
        ModSounds.SOUND_EVENTS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus);
        ModMenus.MENUS.register(modBus);
        modBus.addListener(ModCapabilities::register);
        modBus.addListener(ModPayloads::register);
        BuiltinRefineries.register();
    }
}
