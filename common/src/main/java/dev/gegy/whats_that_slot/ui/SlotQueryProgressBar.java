package dev.gegy.whats_that_slot.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.Slot;

public final class SlotQueryProgressBar {
    private static final int WIDTH = 16;
    private static final int HEIGHT = 2;

    private static final int COLOR = 0xFFF0F0F0;

    private static final float MAX_PROGRESS = (float) (SlotQueryInput.REQUEST_TICKS - 3) / SlotQueryInput.REQUEST_TICKS;

    private final int x0;
    private final int y0;

    public SlotQueryProgressBar(AbstractContainerScreen<?> screen, Slot slot) {
        var screenBounds = Bounds2i.ofScreen(screen);
        this.x0 = screenBounds.x0() + slot.x;
        this.y0 = screenBounds.y0() + slot.y;
    }

    public void draw(GuiGraphics graphics, float progress) {
        float displayProgress = computeDisplayProgress(progress);
        if (displayProgress > 0.0F) {
            int x1 = this.x0 + Math.round(WIDTH * displayProgress);
            int y1 = this.y0 + HEIGHT;
            graphics.fill(RenderType.guiOverlay(), this.x0, this.y0, x1, y1, COLOR);
        }
    }

    private static float computeDisplayProgress(float progress) {
        if (progress >= MAX_PROGRESS) return 1.0F;

        float scaled = progress / MAX_PROGRESS;
        return scaled * scaled * scaled;
    }
}
