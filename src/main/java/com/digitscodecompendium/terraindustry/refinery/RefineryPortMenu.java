package com.digitscodecompendium.terraindustry.refinery;

import com.digitscodecompendium.terraindustry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

/** Server-backed storage menu; role and location are sent when opening it. */
public class RefineryPortMenu extends AbstractContainerMenu {
    private final @Nullable RefineryPortBlockEntity port;
    private final RefineryPortType type;
    private int fluidAmount;
    private int fluidTypeId;
    private int modifierActivationProgress;
    private int modifierActivationTicks;
    private int activeModifier;
    private int activeModifierTicks;

    public RefineryPortMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, findPort(inventory.player, buffer.readBlockPos()), RefineryPortType.values()[buffer.readVarInt()]);
    }

    public RefineryPortMenu(int id, Inventory inventory, @Nullable RefineryPortBlockEntity port, RefineryPortType type) {
        super(ModMenus.REFINERY_PORT.get(), id);
        this.port = port;
        this.type = type;
        RefineryResource expectedResource = expectedResource();
        if (port != null && (type == RefineryPortType.MODIFIER
                || expectedResource != null && expectedResource.kind() == RefineryResource.Kind.ITEM)) {
            addPortSlots(port);
        }
        addFluidDataSlots();
        addModifierDataSlots();
        addPlayerInventory(inventory);
    }

    private void addPortSlots(RefineryPortBlockEntity port) {
        if (type == RefineryPortType.MODIFIER) {
            addSlot(new SlotItemHandler(port.itemStorage(), 0, 80, 58) {
                @Override public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
                    return super.mayPlace(stack);
                }
            });
            return;
        }
        boolean acceptsItems = type == RefineryPortType.FUEL || type == RefineryPortType.MODIFIER;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slotIndex = column + row * 3;
                addSlot(new SlotItemHandler(port.itemStorage(), slotIndex, 62 + column * 18, 58 + row * 18) {
                    @Override
                    public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
                        return acceptsItems;
                    }
                });
            }
        }
    }

    private void addFluidDataSlots() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                var storage = port == null ? null : port.fluidStorage();
                return storage == null ? 0 : storage.getFluidInTank(0).getAmount();
            }

            @Override
            public void set(int value) {
                fluidAmount = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                var storage = port == null ? null : port.fluidStorage();
                return storage == null ? 0 : BuiltInRegistries.FLUID.getId(storage.getFluidInTank(0).getFluid());
            }

            @Override
            public void set(int value) {
                fluidTypeId = value;
            }
        });
    }

    private void addModifierDataSlots() {
        addDataSlot(new DataSlot() {
            @Override public int get() { return port == null ? 0 : port.modifierActivationProgress(); }
            @Override public void set(int value) { modifierActivationProgress = value; }
        });
        addDataSlot(new DataSlot() {
            @Override public int get() { return port == null ? 0 : port.modifierActivationTicks(); }
            @Override public void set(int value) { modifierActivationTicks = value; }
        });
        addDataSlot(new DataSlot() {
            @Override public int get() { return port == null ? 0 : port.activeModifier().ordinal(); }
            @Override public void set(int value) { activeModifier = value; }
        });
        addDataSlot(new DataSlot() {
            @Override public int get() { return port == null ? 0 : port.activeModifierTicksRemaining(); }
            @Override public void set(int value) { activeModifierTicks = value; }
        });
    }

    private static @Nullable RefineryPortBlockEntity findPort(Player player, BlockPos pos) {
        return player.level().getBlockEntity(pos) instanceof RefineryPortBlockEntity port ? port : null;
    }

    public RefineryPortType portType() {
        return type;
    }

    public @Nullable RefineryPortBlockEntity port() {
        return port;
    }

    public int fluidAmount() {
        return fluidAmount;
    }

    public int fluidTypeId() {
        return fluidTypeId;
    }

    public int modifierActivationProgress() { return modifierActivationProgress; }
    public int modifierActivationTicks() { return modifierActivationTicks; }
    public int activeModifierTicks() { return activeModifierTicks; }
    public RefineryModifierType activeModifier() {
        RefineryModifierType[] values = RefineryModifierType.values();
        return activeModifier >= 0 && activeModifier < values.length
                ? values[activeModifier] : RefineryModifierType.NONE;
    }

    public @Nullable RefineryOperatingRate operatingRate() {
        if (type != RefineryPortType.FUEL) {
            return null;
        }
        return RefineryDefinitions.defaultId().flatMap(RefineryDefinitions::find)
                .map(RefineryDefinition::fuel)
                .orElse(null);
    }

    public @Nullable RefineryResource expectedResource() {
        RefineryOperatingRate rate = operatingRate();
        return rate == null ? null : rate.resource();
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new net.minecraft.world.inventory.Slot(inventory, column + row * 9 + 9,
                        8 + column * 18, 140 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new net.minecraft.world.inventory.Slot(inventory, column, 8 + column * 18, 198));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return port != null && player.distanceToSqr(port.getBlockPos().getCenter()) <= 64.0D;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}
