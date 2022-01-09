package dev.gegy.whats_that_slot.ui.draw;

import dev.gegy.whats_that_slot.ui.scroll.ScrollView;

public record SlotGridLayout(
        int x0, int y0,
        int countX, int countY
) {
    public static final int SLOT_SIZE = 18;

    public ScrollView createScrollView(int itemCount) {
        int contentHeight = this.contentHeight(itemCount);
        return new ScrollView(this.countY, contentHeight);
    }

    public int contentHeight(int itemCount) {
        return (itemCount + this.countX - 1) / this.countX;
    }

    public int screenX(int slotX) {
        return slotX * SLOT_SIZE + this.x0;
    }

    public int screenY(int slotY) {
        return slotY * SLOT_SIZE + this.y0;
    }

    public int slotX(int screenX) {
        return Math.floorDiv(screenX - this.x0, SLOT_SIZE);
    }

    public int slotY(int screenY) {
        return Math.floorDiv(screenY - this.y0, SLOT_SIZE);
    }

    public boolean contains(int slotX, int slotY) {
        return slotX >= 0 && slotY >= 0
                && slotX < this.countX && slotY < this.countY;
    }

    public int index(int slotX, int slotY) {
        return slotX + slotY * this.countX;
    }

    public int count() {
        return this.countX * this.countY;
    }

    public void forEach(ForEach forEach) {
        for (int y = 0; y < this.countY; y++) {
            for (int x = 0; x < this.countX; x++) {
                forEach.accept(this.index(x, y), x, y);
            }
        }
    }

    public interface ForEach {
        void accept(int index, int slotX, int slotY);
    }
}
