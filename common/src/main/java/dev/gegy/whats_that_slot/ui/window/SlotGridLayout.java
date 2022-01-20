package dev.gegy.whats_that_slot.ui.window;

import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.scroll.ScrollView;
import net.minecraft.util.Mth;

public record SlotGridLayout(int countX, int countY) {
    public static final int SLOT_SIZE = 18;

    public static SlotGridLayout createVertical(int countX, int maxCountY, int itemCount) {
        int slotsCountY = Mth.clamp(contentHeight(countX, itemCount), 1, maxCountY);
        return new SlotGridLayout(countX, slotsCountY);
    }

    private static int contentHeight(int countX, int itemCount) {
        return (itemCount + countX - 1) / countX;
    }

    public ScrollView createScrollView(int itemCount) {
        int contentHeight = this.contentHeight(itemCount);
        return new ScrollView(this.countY, contentHeight);
    }

    public int contentHeight(int itemCount) {
        return contentHeight(this.countX, itemCount);
    }

    public int screenX(int slotX) {
        return slotX * SLOT_SIZE;
    }

    public int screenY(int slotY) {
        return slotY * SLOT_SIZE;
    }

    public int slotX(int screenX) {
        return Math.floorDiv(screenX, SLOT_SIZE);
    }

    public int slotY(int screenY) {
        return Math.floorDiv(screenY, SLOT_SIZE);
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

    public int screenWidth() {
        return this.countX * SLOT_SIZE;
    }

    public int screenHeight() {
        return this.countY * SLOT_SIZE;
    }

    public Bounds2i screenBounds(int x0, int y0) {
        return Bounds2i.ofSize(x0, y0, this.screenWidth(), this.screenHeight());
    }

    public interface ForEach {
        void accept(int index, int slotX, int slotY);
    }
}
