package dev.gegy.whats_that_slot.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.SlotQueryInput;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

public final class SlotQueryProgressBar extends GuiComponent {
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

    public void draw(PoseStack matrices, float progress) {
        float displayProgress = computeDisplayProgress(progress);
        if (displayProgress > 0.0F) {
            int x1 = this.x0 + Math.round(WIDTH * displayProgress);
            int y1 = this.y0 + HEIGHT;
            fill(matrices, this.x0, this.y0, x1, y1, COLOR);
        }
    }

    private static float computeDisplayProgress(float progress) {
        if (progress >= MAX_PROGRESS) return 1.0F;

        float scaled = progress / MAX_PROGRESS;
        return scaled * scaled * scaled;
    }
}
