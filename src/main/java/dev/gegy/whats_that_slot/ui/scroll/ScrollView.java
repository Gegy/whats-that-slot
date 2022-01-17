package dev.gegy.whats_that_slot.ui.scroll;

import dev.gegy.whats_that_slot.ui.Bounds2i;
import net.minecraft.util.math.MathHelper;

public final class ScrollView {
    private final int viewHeight;
    private final int contentHeight;

    private float scroll;

    public ScrollView(int viewHeight, int contentHeight) {
        this.viewHeight = viewHeight;
        this.contentHeight = contentHeight;
    }

    public void setScroll(float scroll) {
        this.scroll = MathHelper.clamp(scroll, 0.0F, this.maxScroll());
    }

    public void mouseScrolled(double amount) {
        this.setScroll(this.scroll - (float) amount);
    }

    public float scroll() {
        return this.scroll;
    }

    public int scroller(Scrollbar scrollbar) {
        return this.scrollToScroller(this.scroll, scrollbar);
    }

    public Bounds2i scrollerFromTop(Scrollbar scrollbar, Bounds2i topBounds) {
        int scrollerY = this.scroller(scrollbar);
        return topBounds.offset(0, scrollerY);
    }

    public float scrollerToScroll(int scroller, Scrollbar bar) {
        return scroller * this.maxScroll() / bar.maxScroller();
    }

    public int scrollToScroller(float scroll, Scrollbar bar) {
        return Math.round(scroll * bar.maxScroller() / this.maxScroll());
    }

    public float maxScroll() {
        return Math.max(this.contentHeight - this.viewHeight, 0);
    }

    public boolean canScroll() {
        return this.maxScroll() > 0;
    }

    public int viewHeight() {
        return this.viewHeight;
    }

    public int contentHeight() {
        return this.contentHeight;
    }
}
