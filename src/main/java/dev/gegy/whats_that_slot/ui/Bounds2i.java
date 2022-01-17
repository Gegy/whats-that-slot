package dev.gegy.whats_that_slot.ui;

import dev.gegy.whats_that_slot.mixin.AbstractContainerScreenAccess;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public final class Bounds2i {
    private final int x0;
    private final int y0;
    private final int x1;
    private final int y1;

    public Bounds2i(int x0, int y0, int x1, int y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public static Bounds2i ofScreen(AbstractContainerScreen<?> screen) {
        AbstractContainerScreenAccess access = (AbstractContainerScreenAccess) screen;
        return new Bounds2i(
                access.getLeftPos(), access.getTopPos(),
                access.getLeftPos() + access.getImageWidth(), access.getTopPos() + access.getImageHeight()
        );
    }

    public static Bounds2i ofSize(int x, int y, int width, int height) {
        return new Bounds2i(x, y, x + width, y + height);
    }

    public static Bounds2i shiftToFitInScreen(AbstractContainerScreen<?> screen, Bounds2i bounds) {
        if (bounds.x1() > screen.width) {
            bounds = bounds.offset(-bounds.width(), 0);
        }

        if (bounds.y1() > screen.height) {
            bounds = bounds.offset(0, -bounds.height());
        }

        return bounds;
    }

    public int x0() {
        return this.x0;
    }

    public int y0() {
        return this.y0;
    }

    public int x1() {
        return this.x1;
    }

    public int y1() {
        return this.y1;
    }

    public int width() {
        return this.x1 - this.x0;
    }

    public int height() {
        return this.y1 - this.y0;
    }

    public boolean contains(double x, double y) {
        return x >= this.x0 && y >= this.y0
                && x < this.x1 && y < this.y1;
    }

    public boolean contains(int x, int y) {
        return x >= this.x0 && y >= this.y0
                && x < this.x1 && y < this.y1;
    }

    public Bounds2i offset(int x, int y) {
        return new Bounds2i(this.x0 + x, this.y0 + y, this.x1 + x, this.y1 + y);
    }
}
