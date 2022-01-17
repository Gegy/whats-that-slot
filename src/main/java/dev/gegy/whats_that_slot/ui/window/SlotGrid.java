package dev.gegy.whats_that_slot.ui.window;

import dev.gegy.whats_that_slot.query.QueriedItem;
import dev.gegy.whats_that_slot.ui.scroll.ScrollView;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.List;

public final class SlotGrid {
    private final SlotGridLayout layout;
    private final List<QueriedItem> items;

    private int scrollIndexOffset;

    public SlotGrid(SlotGridLayout layout, List<QueriedItem> items) {
        this.layout = layout;
        this.items = items;
    }

    public ScrollView createScrollView() {
        return this.layout.createScrollView(this.items.size());
    }

    public void applyScroll(float scroll) {
        this.scrollIndexOffset = MathHelper.floor(scroll) * this.layout.countX();
    }

    @Nullable
    public QueriedItem get(int x, int y) {
        if (this.layout.contains(x, y)) {
            return this.get(this.layout.index(x, y));
        } else {
            return null;
        }
    }

    @Nullable
    public QueriedItem get(int index) {
        int listIndex = index + this.scrollIndexOffset;
        if (listIndex >= 0 && listIndex < this.items.size()) {
            return this.items.get(listIndex);
        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }
}
