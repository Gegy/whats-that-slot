package dev.gegy.whats_that_slot.ui.scroll;

public final class Scrollbar {
    private final int scrollbarHeight;
    private final int scrollerHeight;

    public Scrollbar(int scrollbarHeight, int scrollerHeight) {
        this.scrollbarHeight = scrollbarHeight;
        this.scrollerHeight = scrollerHeight;
    }

    public int scrollbarHeight() {
        return this.scrollbarHeight;
    }

    public int scrollerHeight() {
        return this.scrollerHeight;
    }

    public int maxScroller() {
        return this.scrollbarHeight - this.scrollerHeight;
    }
}
