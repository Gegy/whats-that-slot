package dev.gegy.whats_that_slot.query;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public final class SlotQuery {
    private final List<QueriedItem> items;

    SlotQuery(List<QueriedItem> items) {
        this.items = items;
    }

    public static SlotQueryGenerator forSlot(AbstractContainerScreen<?> screen, Slot slot) {
        Predicate<ItemStack> filter = SlotFilter.includedBy(slot);
        return new SlotQueryGenerator(screen, filter);
    }

    public List<QueriedItem> items() {
        return this.items;
    }
}
