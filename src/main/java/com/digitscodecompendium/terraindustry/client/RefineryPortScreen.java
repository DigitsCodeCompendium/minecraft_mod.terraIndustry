package com.digitscodecompendium.terraindustry.client;

import com.digitscodecompendium.terraindustry.refinery.RefineryPortMenu;
import com.digitscodecompendium.terraindustry.refinery.RefineryPortType;
import com.digitscodecompendium.terraindustry.refinery.RefineryResource;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class RefineryPortScreen extends AbstractContainerScreen<RefineryPortMenu> {
    public RefineryPortScreen(RefineryPortMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 222;
        titleLabelY = -100;
        inventoryLabelY = -100;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RefineryUiRenderer.panel(graphics, leftPos, topPos, imageWidth, imageHeight, title.getString());
        graphics.drawString(font, title, leftPos + 13, topPos + 11, 0xFFF1E0BE, false);

        RefineryResource expected = menu.expectedResource();
        if (expected != null && expected.kind() == RefineryResource.Kind.ITEM) {
            drawItemStorage(graphics, expected);
        } else if (isFluidPort(expected)) {
            drawFluidStorage(graphics, expected);
        } else {
            graphics.drawString(font, "No configured resource", leftPos + 13, topPos + 54, 0xFFE0E0E0, false);
        }

        if (expected != null) {
            drawQuantityBadge(graphics);
        }

        drawPlayerInventory(graphics);
    }

    private void drawItemStorage(GuiGraphics graphics, RefineryResource expected) {
        RefineryUiRenderer.previewPlaque(graphics, leftPos + 79, topPos + 30, menu.portType());
        graphics.renderItem(new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(expected.id())),
                leftPos + 80, topPos + 31);
        drawSlotGrid(graphics, 3, 3, leftPos + 61, topPos + 57);
    }

    private boolean isFluidPort(RefineryResource expected) {
        return menu.portType() == RefineryPortType.COOLANT
                || expected != null && expected.kind() == RefineryResource.Kind.FLUID;
    }

    private void drawFluidStorage(GuiGraphics graphics, RefineryResource expected) {
        int amount = menu.fluidAmount();
        int height = Math.min(50, amount * 50 / 16_000);
        RefineryUiRenderer.recessedPanel(graphics, leftPos + 78, topPos + 17, 20, 55);
        graphics.fill(leftPos + 81, topPos + 69 - height, leftPos + 95, topPos + 69, 0xFF3F76E4);

        String label = expected == null ? "COOLANT INPUT" : "INPUT: " + expected.id().getPath();
        graphics.drawString(font, label, leftPos + 13, topPos + 78, 0xFFC9AA77, false);
        graphics.drawString(font, amount + " / 16000 mB", leftPos + 13, topPos + 90, 0xFFE0E0E0, false);

        var currentFluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.byId(menu.fluidTypeId());
        if (amount > 0 && currentFluid != null) {
            graphics.drawString(font, currentFluid.getFluidType().getDescription(), leftPos + 13, topPos + 102,
                    0xFFE0E0E0, false);
        }
    }

    private void drawPlayerInventory(GuiGraphics graphics) {
        graphics.drawString(font, playerInventoryTitle, leftPos + 13, topPos + 128, 0xFFC9AA77, false);
        drawSlotGrid(graphics, 9, 3, leftPos + 7, topPos + 139);
        drawSlotGrid(graphics, 9, 1, leftPos + 7, topPos + 197);
    }

    private void drawSlotGrid(GuiGraphics graphics, int columns, int rows, int x, int y) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                RefineryUiRenderer.slot(graphics, x + column * 18, y + row * 18);
            }
        }
    }

    private void drawQuantityBadge(GuiGraphics graphics) {
        var rate = menu.operatingRate();
        if (rate == null) {
            return;
        }

        String label = rate.resource().amount() + "/" + rate.intervalTicks() + "t";
        int x = leftPos + 105;
        int y = topPos + 36;
        int width = font.width(label) + 4;
        graphics.fill(x - 2, y - 2, x + width, y + 10, 0xFF17130F);
        graphics.fill(x - 1, y - 1, x + width - 1, y + 9, 0xFF4A3420);
        graphics.drawString(font, label, x, y, 0xFFF1E0BE, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
