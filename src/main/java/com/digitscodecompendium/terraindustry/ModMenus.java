package com.digitscodecompendium.terraindustry;

import com.digitscodecompendium.terraindustry.refinery.RefineryControllerMenu;
import com.digitscodecompendium.terraindustry.refinery.RefineryPortMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, TerraIndustry.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<RefineryPortMenu>> REFINERY_PORT =
            MENUS.register("refinery_port", () -> IMenuTypeExtension.create(RefineryPortMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<RefineryControllerMenu>> REFINERY_CONTROLLER =
            MENUS.register("refinery_controller", () -> IMenuTypeExtension.create(RefineryControllerMenu::new));
    private ModMenus() { }
}
