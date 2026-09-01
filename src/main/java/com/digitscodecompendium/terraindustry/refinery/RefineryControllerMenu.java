package com.digitscodecompendium.terraindustry.refinery;

import com.digitscodecompendium.terraindustry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import org.jetbrains.annotations.Nullable;

/** Read-only status menu for the chunk's refinery brain. */
public class RefineryControllerMenu extends AbstractContainerMenu {
    private final @Nullable RefineryControllerBlockEntity controller;
    private int active;
    private int progress;
    private int portCount;
    private int cycleTicks;
    private int activeHourMask;
    private int modifierType;
    private int modifierTicksRemaining;
    private int accelerationModifiers;
    private int sabotageModifiers;
    private int crystallizationModifiers;
    public RefineryControllerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, find(inventory.player, buffer.readBlockPos()));
    }

    public RefineryControllerMenu(int id, Inventory inventory, @Nullable RefineryControllerBlockEntity controller) {
        super(ModMenus.REFINERY_CONTROLLER.get(), id);
        this.controller = controller;
        addControllerDataSlots();
    }

    private void addControllerDataSlots() {
        addDataSlot(syncing(() -> controller != null && controller.isActive() ? 1 : 0, value -> active = value));
        addDataSlot(syncing(() -> controller == null ? 0 : controller.progress(), value -> progress = value));
        addDataSlot(syncing(() -> controller == null ? 0 : controller.ports().size(), value -> portCount = value));
        addDataSlot(syncing(() -> controller == null ? 0 : controller.cycleTicks(), value -> cycleTicks = value));
        addDataSlot(syncing(() -> controller == null ? 0 : controller.activeHourMask(), value -> activeHourMask = value));
        addDataSlot(syncing(() -> controller == null ? 0 : controller.activeModifier().ordinal(),
                value -> modifierType = value));
        addDataSlot(syncing(() -> controller == null ? 0
                        : controller.modifierTicksRemaining(RefineryModifierType.SABOTAGE),
                value -> modifierTicksRemaining = value));
        addDataSlot(syncing(() -> serverModifierCount(RefineryModifierType.ACCELERATION),
                value -> accelerationModifiers = value));
        addDataSlot(syncing(() -> serverModifierCount(RefineryModifierType.SABOTAGE),
                value -> sabotageModifiers = value));
        addDataSlot(syncing(() -> serverModifierCount(RefineryModifierType.CRYSTALLIZATION),
                value -> crystallizationModifiers = value));
    }

    private static DataSlot syncing(java.util.function.IntSupplier getter, java.util.function.IntConsumer setter) {
        return new DataSlot() {
            @Override
            public int get() {
                return getter.getAsInt();
            }

            @Override
            public void set(int value) {
                setter.accept(value);
            }
        };
    }

    private int serverModifierCount(RefineryModifierType type) {
        return controller == null ? 0 : controller.modifierCount(type);
    }

    private static @Nullable RefineryControllerBlockEntity find(Player player, BlockPos pos) {
        return player.level().getBlockEntity(pos) instanceof RefineryControllerBlockEntity controller ? controller : null;
    }

    public @Nullable RefineryControllerBlockEntity controller() {
        return controller;
    }

    public boolean active() {
        return active != 0;
    }

    public int progress() {
        return progress;
    }

    public int portCount() {
        return portCount;
    }

    public int cycleTicks() {
        return cycleTicks;
    }

    public int activeHourMask() {
        return activeHourMask;
    }

    public RefineryModifierType activeModifier() {
        RefineryModifierType[] values = RefineryModifierType.values();
        return modifierType >= 0 && modifierType < values.length ? values[modifierType] : RefineryModifierType.NONE;
    }

    public int modifierTicksRemaining() {
        return modifierTicksRemaining;
    }

    public int modifierCount(RefineryModifierType type) {
        return switch (type) {
            case ACCELERATION -> accelerationModifiers;
            case SABOTAGE -> sabotageModifiers;
            case CRYSTALLIZATION -> crystallizationModifiers;
            case NONE -> 0;
        };
    }

    @Override
    public boolean stillValid(Player player) {
        return controller != null && player.distanceToSqr(controller.getBlockPos().getCenter()) <= 64.0D;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}
