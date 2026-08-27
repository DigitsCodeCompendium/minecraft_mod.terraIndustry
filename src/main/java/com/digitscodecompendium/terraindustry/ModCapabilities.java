package com.digitscodecompendium.terraindustry;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/** Exposes each port's native storage to pipes, hoppers and energy networks. */
public final class ModCapabilities {
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.REFINERY_PORT.get(),
                (port, side) -> port.itemStorage());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.REFINERY_PORT.get(),
                (port, side) -> port.fluidStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.REFINERY_PORT.get(),
                (port, side) -> port.energyStorage());
    }

    private ModCapabilities() { }
}
