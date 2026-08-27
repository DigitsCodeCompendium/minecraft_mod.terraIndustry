package com.digitscodecompendium.terraindustry.client;

import net.minecraft.client.gui.GuiGraphics;
import com.digitscodecompendium.terraindustry.refinery.RefineryPortType;

/** Code-drawn, Minecraft-inspired panels. Keeping this procedural makes every machine skin-free. */
final class RefineryUiRenderer {
    private static final int OUTLINE = 0xFF17130F;
    private static final int BRONZE_DARK = 0xFF4A3420;
    private static final int BRONZE_LIGHT = 0xFF9B7542;
    private static final int METAL_DARK = 0xFF282A2B;
    private static final int METAL = 0xFF464A4C;
    private static final int METAL_LIGHT = 0xFF73787B;

    static void panel(GuiGraphics graphics, int x, int y, int width, int height, String title) {
        graphics.fill(x, y, x + width, y + height, OUTLINE);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, BRONZE_DARK);
        graphics.fill(x + 3, y + 3, x + width - 3, y + height - 3, METAL_DARK);
        graphics.fill(x + 5, y + 5, x + width - 5, y + 22, BRONZE_DARK);
        graphics.fill(x + 6, y + 6, x + width - 6, y + 7, BRONZE_LIGHT);
        // Corner rivets make the frame read as a Minecraft machine rather than a flat dialog.
        for (int[] rivet : new int[][] {{8, 8}, {width - 11, 8}, {8, height - 11}, {width - 11, height - 11}}) {
            graphics.fill(x + rivet[0], y + rivet[1], x + rivet[0] + 3, y + rivet[1] + 3, BRONZE_LIGHT);
        }
    }

    static void recessedPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, OUTLINE);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF1A1C1D);
        graphics.fill(x + 2, y + 2, x + width - 2, y + 3, 0xFF111213);
    }

    static void slot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 19, y + 19, OUTLINE);
        graphics.fill(x, y, x + 18, y + 18, METAL_LIGHT);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF232526);
        graphics.fill(x + 2, y + 2, x + 16, y + 3, 0xFF121314);
    }

    static void previewPlaque(GuiGraphics graphics, int x, int y, RefineryPortType type) {
        int accent = switch (type) {
            case FUEL -> 0xFFD47A2D;
            case MODIFIER -> 0xFF45A8B9;
            case COOLANT -> 0xFF4E91C8;
        };
        graphics.fill(x - 5, y - 5, x + 23, y + 23, OUTLINE);
        graphics.fill(x - 4, y - 4, x + 22, y + 22, accent);
        graphics.fill(x - 2, y - 2, x + 20, y + 20, 0xFF232526);
        // Distinct corner marks keep plaques recognizable even before reading their item.
        graphics.fill(x - 3, y - 3, x + 3, y, accent);
        graphics.fill(x + 17, y + 17, x + 21, y + 20, accent);
        graphics.fill(x + 17, y - 3, x + 21, y + 1, accent);
        graphics.fill(x - 3, y + 17, x + 1, y + 21, accent);
    }

    static void indicator(GuiGraphics graphics, int x, int y, boolean lit) {
        graphics.fill(x, y, x + 10, y + 10, OUTLINE);
        graphics.fill(x + 2, y + 2, x + 8, y + 8, lit ? 0xFF63C944 : 0xFF803232);
        graphics.fill(x + 3, y + 3, x + 6, y + 4, lit ? 0xFFA7F58A : 0xFFC87878);
    }

    private RefineryUiRenderer() { }
}
