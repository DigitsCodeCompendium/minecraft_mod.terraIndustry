package com.digitscodecompendium.terraindustry.effects;

import com.digitscodecompendium.terraindustry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import org.jetbrains.annotations.Nullable;

/** A no-inventory menu used to synchronize effects-block settings to its configuration screen. */
public class EffectsBlockMenu extends AbstractContainerMenu {
    private final @Nullable EffectsBlockEntity effects;
    private final BlockPos effectsPos;
    private int finalRadiusTenths;
    private int fadeExponentHundredths;
    private int pulseDurationTicks;
    private int pulseColor;
    private int segmentSizeTenths;
    private int segmentSpacingTenths;

    public EffectsBlockMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, buffer.readBlockPos());
    }

    private EffectsBlockMenu(int id, Inventory inventory, BlockPos effectsPos) {
        this(id, inventory, findEffects(inventory.player, effectsPos), effectsPos);
    }

    public EffectsBlockMenu(int id, Inventory inventory, @Nullable EffectsBlockEntity effects) {
        this(id, inventory, effects, effects == null ? BlockPos.ZERO : effects.getBlockPos());
    }

    private EffectsBlockMenu(int id, Inventory inventory, @Nullable EffectsBlockEntity effects, BlockPos effectsPos) {
        super(ModMenus.EFFECTS_BLOCK.get(), id);
        this.effects = effects;
        this.effectsPos = effectsPos;
        addConfigurationDataSlots();
    }

    private void addConfigurationDataSlots() {
        addDataSlot(syncing(() -> effects == null ? 0 : effects.finalRadiusTenths(), value -> finalRadiusTenths = value));
        addDataSlot(syncing(
                () -> effects == null ? 0 : effects.fadeExponentHundredths(), value -> fadeExponentHundredths = value));
        addDataSlot(syncing(() -> effects == null ? 0 : effects.pulseDurationTicks(), value -> pulseDurationTicks = value));
        addDataSlot(syncing(() -> effects == null ? 0 : effects.pulseColor(), value -> pulseColor = value));
        addDataSlot(syncing(() -> effects == null ? 0 : effects.segmentSizeTenths(), value -> segmentSizeTenths = value));
        addDataSlot(syncing(
                () -> effects == null ? 0 : effects.segmentSpacingTenths(), value -> segmentSpacingTenths = value));
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

    private static @Nullable EffectsBlockEntity findEffects(Player player, BlockPos pos) {
        return player.level().getBlockEntity(pos) instanceof EffectsBlockEntity effects ? effects : null;
    }

    public BlockPos effectsPos() {
        return effectsPos;
    }

    public int finalRadiusTenths() {
        return finalRadiusTenths;
    }

    public int fadeExponentHundredths() {
        return fadeExponentHundredths;
    }

    public int pulseDurationTicks() {
        return pulseDurationTicks;
    }

    public int pulseColor() {
        return pulseColor;
    }

    public int segmentSizeTenths() {
        return segmentSizeTenths;
    }

    public int segmentSpacingTenths() {
        return segmentSpacingTenths;
    }

    @Override
    public boolean stillValid(Player player) {
        return effects != null && player.distanceToSqr(effects.getBlockPos().getCenter()) <= 64.0D;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}
