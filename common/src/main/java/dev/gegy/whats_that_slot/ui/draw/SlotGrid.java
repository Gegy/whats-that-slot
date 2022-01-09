package dev.gegy.whats_that_slot.ui.draw;

import dev.gegy.whats_that_slot.ui.scroll.ScrollView;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class SlotGrid {
    private final SlotGridLayout layout;
    private final List<ItemStack> items;

    private int scrollIndexOffset;

    public SlotGrid(SlotGridLayout layout, List<ItemStack> items) {
        this.layout = layout;
        this.items = items;
    }

    public ScrollView createScrollView() {
        return this.layout.createScrollView(this.items.size());
    }

    public void applyScroll(float scroll) {
        this.scrollIndexOffset = Mth.floor(scroll) * this.layout.countX();
    }

    public ItemStack get(int index) {
        int listIndex = index + this.scrollIndexOffset;
        if (listIndex >= 0 && listIndex < this.items.size()) {
            return this.items.get(listIndex);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }
}
