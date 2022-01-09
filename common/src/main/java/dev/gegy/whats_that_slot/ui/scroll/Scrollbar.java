package dev.gegy.whats_that_slot.ui.scroll;

public record Scrollbar(
        int scrollbarHeight,
        int scrollerHeight
) {
    public int maxScroller() {
        return this.scrollbarHeight - this.scrollerHeight;
    }
}
