package com.digitscodecompendium.terraindustry.refinery;

import com.digitscodecompendium.terraindustry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/** A passive port. The controller grants it an owner only while it shares that owner's chunk. */
public class RefineryPortBlockEntity extends BlockEntity {
    public static final int FLUID_CAPACITY = 16_000;
    public static final int ENERGY_CAPACITY = 100_000;
    private @Nullable BlockPos controllerPos;
    private final ItemStackHandler items = new ItemStackHandler(9) {
        @Override protected void onContentsChanged(int slot) { refineryContentsChanged(); }

        @Override public void deserializeNBT(HolderLookup.Provider registries, CompoundTag tag) {
            super.deserializeNBT(registries, tag);
            // Ports saved before the 3×3 upgrade contain Size: 1. Preserve their stack while
            // expanding the backing list, otherwise opening the nine-slot menu crashes.
            if (stacks.size() < 9) {
                var previous = new ArrayList<>(stacks);
                stacks = NonNullList.withSize(9, ItemStack.EMPTY);
                for (int slot = 0; slot < previous.size(); slot++) {
                    stacks.set(slot, previous.get(slot));
                }
            }
        }
    };
    private final FluidTank fluid = new FluidTank(FLUID_CAPACITY) {
        @Override protected void onContentsChanged() { refineryContentsChanged(); }
    };
    private final EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY) {
        @Override public int receiveEnergy(int amount, boolean simulate) {
            int accepted = super.receiveEnergy(amount, simulate);
            if (accepted > 0 && !simulate) refineryContentsChanged();
            return accepted;
        }
        @Override public int extractEnergy(int amount, boolean simulate) {
            int extracted = super.extractEnergy(amount, simulate);
            if (extracted > 0 && !simulate) refineryContentsChanged();
            return extracted;
        }
    };

    public RefineryPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINERY_PORT.get(), pos, state);
    }

    public @Nullable BlockPos controllerPos() { return controllerPos; }
    public RefineryPortType portType() { return ((RefineryPortBlock) getBlockState().getBlock()).portType(); }
    public IItemHandler itemStorage() { return items; }
    public @Nullable IFluidHandler fluidStorage() { return portType() == RefineryPortType.MODIFIER ? null : fluid; }
    public @Nullable IEnergyStorage energyStorage() {
        return switch (portType()) {
            case FUEL -> energy;
            default -> null;
        };
    }

    void linkTo(@Nullable BlockPos controllerPos) {
        if (!Objects.equals(this.controllerPos, controllerPos)) {
            this.controllerPos = controllerPos;
            setChanged();
        }
    }

    private void refineryContentsChanged() {
        setChanged();
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (controllerPos != null) tag.putLong("Controller", controllerPos.asLong());
        tag.put("Items", items.serializeNBT(registries));
        tag.put("Fluid", fluid.writeToNBT(registries, new CompoundTag()));
        tag.put("Energy", energy.serializeNBT(registries));
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        controllerPos = tag.contains("Controller") ? BlockPos.of(tag.getLong("Controller")) : null;
        if (tag.contains("Items")) items.deserializeNBT(registries, tag.getCompound("Items"));
        if (tag.contains("Fluid")) fluid.readFromNBT(registries, tag.getCompound("Fluid"));
        if (tag.contains("Energy")) energy.deserializeNBT(registries, tag.get("Energy"));
    }
}
