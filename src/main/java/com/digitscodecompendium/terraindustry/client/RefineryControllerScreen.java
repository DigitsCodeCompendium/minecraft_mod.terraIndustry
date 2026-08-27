package com.digitscodecompendium.terraindustry.client;

import com.digitscodecompendium.terraindustry.refinery.RefineryControllerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class RefineryControllerScreen extends AbstractContainerScreen<RefineryControllerMenu> {
    public RefineryControllerScreen(RefineryControllerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
        titleLabelY = -100;
        inventoryLabelY = -100;
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RefineryUiRenderer.panel(graphics, leftPos, topPos, imageWidth, imageHeight, title.getString());
        graphics.drawString(font, title, leftPos + 13, topPos + 11, 0xFFF1E0BE, false);
        RefineryUiRenderer.recessedPanel(graphics, leftPos + 12, topPos + 31, 152, 74);
        RefineryUiRenderer.indicator(graphics, leftPos + 22, topPos + 43, menu.active());
        graphics.drawString(font, menu.active() ? "REFINERY ACTIVE" : "REFINERY STANDBY", leftPos + 38, topPos + 43, menu.active() ? 0xFF8BE06E : 0xFFE08B6E, false);
        graphics.drawString(font, "Connected ports: " + menu.portCount(), leftPos + 22, topPos + 62, 0xFFE0E0E0, false);
        graphics.drawString(font, "Cycle: " + cyclePercent() + "%", leftPos + 22, topPos + 79, 0xFFE0E0E0, false);
        graphics.drawString(font, "Remaining: " + remainingTime(), leftPos + 22, topPos + 94, 0xFFE0E0E0, false);
        // A small, familiar machine-style progress meter.
        graphics.fill(leftPos + 22, topPos + 114, leftPos + 154, topPos + 119, 0xFF111213);
        int progressWidth = Math.round(130.0F * cyclePercent() / 100.0F);
        graphics.fill(leftPos + 23, topPos + 115, leftPos + 23 + progressWidth, topPos + 118, menu.active() ? 0xFFB87931 : 0xFF4A3420);
        drawSchedule(graphics);
    }

    private void drawSchedule(GuiGraphics graphics) {
        int x = leftPos + 22;
        int y = topPos + 132;
        int cellWidth = 5;
        int mask = menu.activeHourMask();
        ZoneId localZone = ZoneId.systemDefault();
        ZonedDateTime now = Instant.now().atZone(localZone);
        LocalDate today = now.toLocalDate();
        graphics.drawString(font, "ACTIVATION TIME", x, y - 10, 0xFFC9AA77, false);
        for (int localHour = 0; localHour < 24; localHour++) {
            int utcHour = ZonedDateTime.of(today, LocalTime.of(localHour, 0), localZone)
                    .withZoneSameInstant(ZoneOffset.UTC).getHour();
            boolean activeHour = (mask & (1 << utcHour)) != 0;
            int left = x + localHour * cellWidth;
            boolean majorTick = localHour % 6 == 0;
            graphics.fill(left, y, left + cellWidth - 1, y + 7, activeHour ? 0xFF4E9A44 : 0xFF973A3A);
            graphics.fill(left, y, left + cellWidth - 1, y + 1, activeHour ? 0xFF8BCE72 : 0xFFD06A62);
            graphics.fill(left - 1, y + 8, left, y + (majorTick ? 12 : 10), majorTick ? 0xFFF1E0BE : 0xFF1A1713);
        }
        graphics.fill(x + 119, y + 8, x + 120, y + 12, 0xFFF1E0BE);
        int marker = x + Math.min(119, (now.getHour() * 60 + now.getMinute()) * 120 / 1_440);
        graphics.fill(marker, y - 3, marker + 1, y + 10, 0xFFF1E0BE);
        for (int hour = 0; hour < 24; hour += 6) {
            int labelX = x + hour * cellWidth;
            graphics.pose().pushPose();
            graphics.pose().translate(labelX, y + 13, 0);
            graphics.pose().scale(0.8F, 0.8F, 1.0F);
            graphics.drawString(font, Integer.toString(hour), 0, 0, 0xFFF1E0BE, false);
            graphics.pose().popPose();
        }
        graphics.pose().pushPose();
        graphics.pose().translate(x + 114, y + 13, 0);
        graphics.pose().scale(0.8F, 0.8F, 1.0F);
        graphics.drawString(font, "24", 0, 0, 0xFFF1E0BE, false);
        graphics.pose().popPose();
    }

    private int cyclePercent() {
        return menu.cycleTicks() <= 0 ? 0 : Math.min(100, Math.round(menu.progress() * 100.0F / menu.cycleTicks()));
    }

    private String remainingTime() {
        int remaining = Math.max(0, menu.cycleTicks() - menu.progress());
        int minutes = remaining / 1_200;
        remaining %= 1_200;
        int seconds = remaining / 20;
        int ticks = remaining % 20;
        if (minutes > 0) return minutes + "m " + seconds + "s " + ticks + "t";
        if (seconds > 0) return seconds + "s " + ticks + "t";
        return ticks + "t";
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { renderBackground(graphics, mouseX, mouseY, partialTick); super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
}
