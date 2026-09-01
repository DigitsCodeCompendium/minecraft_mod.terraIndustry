package com.digitscodecompendium.terraindustry.client;

import com.digitscodecompendium.terraindustry.effects.EffectsBlockMenu;
import com.digitscodecompendium.terraindustry.effects.UpdateEffectsConfigPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Administrator screen for the effects block's sonic-pulse settings. */
public class EffectsBlockScreen extends AbstractContainerScreen<EffectsBlockMenu> {
    private EditBox radiusField;
    private EditBox fadeField;
    private EditBox pulseTimeField;
    private EditBox colorField;
    private EditBox segmentSizeField;
    private EditBox segmentSpacingField;
    private String validationMessage = "";
    private boolean configurationLoaded;

    public EffectsBlockScreen(EffectsBlockMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 204;
        imageHeight = 224;
        titleLabelY = -100;
        inventoryLabelY = -100;
    }

    @Override
    protected void init() {
        super.init();
        radiusField = addTextField(leftPos + 125, topPos + 38, formatTenths(menu.finalRadiusTenths()));
        fadeField = addTextField(leftPos + 125, topPos + 62, formatHundredths(menu.fadeExponentHundredths()));
        pulseTimeField = addTextField(leftPos + 125, topPos + 86, formatSeconds(menu.pulseDurationTicks()));
        colorField = addTextField(leftPos + 125, topPos + 110, String.format("#%06X", menu.pulseColor()));
        segmentSizeField = addTextField(leftPos + 125, topPos + 134, formatTenths(menu.segmentSizeTenths()));
        segmentSpacingField = addTextField(leftPos + 125, topPos + 158, formatTenths(menu.segmentSpacingTenths()));
        colorField.setMaxLength(7);
        addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveConfiguration())
                .bounds(leftPos + 112, topPos + 185, 64, 20)
                .build());
    }

    private EditBox addTextField(int x, int y, String initialValue) {
        EditBox field = new EditBox(font, x, y, 54, 18, Component.empty());
        field.setValue(initialValue);
        field.setTextColor(0xFFF1E0BE);
        field.setBordered(true);
        field.setMaxLength(12);
        return addRenderableWidget(field);
    }

    private void saveConfiguration() {
        try {
            float radius = Float.parseFloat(radiusField.getValue());
            float fadeExponent = Float.parseFloat(fadeField.getValue());
            float pulseSeconds = Float.parseFloat(pulseTimeField.getValue());
            float segmentSize = Float.parseFloat(segmentSizeField.getValue());
            float segmentSpacing = Float.parseFloat(segmentSpacingField.getValue());
            int color = parseColor(colorField.getValue());
            if (radius <= 0.0F || fadeExponent <= 0.0F || pulseSeconds <= 0.0F
                    || segmentSize <= 0.0F || segmentSpacing <= 0.0F) {
                throw new IllegalArgumentException();
            }

            PacketDistributor.sendToServer(new UpdateEffectsConfigPayload(
                    menu.effectsPos(), Math.round(radius * 10.0F), Math.round(fadeExponent * 100.0F),
                    Math.round(pulseSeconds * 20.0F), color, Math.round(segmentSize * 10.0F),
                    Math.round(segmentSpacing * 10.0F)));
            validationMessage = "Saved";
        } catch (IllegalArgumentException exception) {
            validationMessage = "Use positive numbers and #RRGGBB.";
        }
    }

    private static int parseColor(String value) {
        String normalized = value.startsWith("#") ? value.substring(1) : value;
        if (!normalized.matches("[0-9a-fA-F]{6}")) {
            throw new IllegalArgumentException("Expected a six-digit hexadecimal color.");
        }
        return Integer.parseInt(normalized, 16);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RefineryUiRenderer.panel(graphics, leftPos, topPos, imageWidth, imageHeight, "Sonic Pulse");
        graphics.drawString(font, "SONIC PULSE", leftPos + 13, topPos + 11, 0xFFF1E0BE, false);
        RefineryUiRenderer.recessedPanel(graphics, leftPos + 12, topPos + 28, 180, 150);

        drawLabel(graphics, "Final radius (blocks)", 38);
        drawLabel(graphics, "Fade exponent", 62);
        drawLabel(graphics, "Pulse time (seconds)", 86);
        drawLabel(graphics, "Pulse color", 110);
        drawLabel(graphics, "Segment size (blocks)", 134);
        drawLabel(graphics, "Segment spacing (blocks)", 158);

        int color = parseDisplayColor();
        graphics.fill(leftPos + 181, topPos + 110, leftPos + 190, topPos + 127, 0xFF000000 | color);
        if (!validationMessage.isEmpty()) {
            graphics.drawString(font, validationMessage, leftPos + 13, topPos + 191,
                    validationMessage.equals("Saved") ? 0xFF8BE06E : 0xFFE08B6E, false);
        }
    }

    private void drawLabel(GuiGraphics graphics, String label, int y) {
        graphics.drawString(font, label, leftPos + 22, topPos + y + 5, 0xFFE0E0E0, false);
    }

    private int parseDisplayColor() {
        try {
            return parseColor(colorField == null ? "#35D4FF" : colorField.getValue());
        } catch (IllegalArgumentException exception) {
            return 0x353535;
        }
    }

    private static String formatTenths(int value) {
        return String.format(java.util.Locale.ROOT, "%.1f", value / 10.0F);
    }

    private static String formatHundredths(int value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value / 100.0F);
    }

    private static String formatSeconds(int ticks) {
        return String.format(java.util.Locale.ROOT, "%.2f", ticks / 20.0F);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (!configurationLoaded && menu.finalRadiusTenths() > 0 && menu.pulseDurationTicks() > 0) {
            radiusField.setValue(formatTenths(menu.finalRadiusTenths()));
            fadeField.setValue(formatHundredths(menu.fadeExponentHundredths()));
            pulseTimeField.setValue(formatSeconds(menu.pulseDurationTicks()));
            colorField.setValue(String.format("#%06X", menu.pulseColor()));
            segmentSizeField.setValue(formatTenths(menu.segmentSizeTenths()));
            segmentSpacingField.setValue(formatTenths(menu.segmentSpacingTenths()));
            configurationLoaded = true;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
