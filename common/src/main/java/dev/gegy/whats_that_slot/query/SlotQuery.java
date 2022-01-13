package dev.gegy.whats_that_slot.query;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public record SlotQuery(List<QueriedItem> items) {
    public static SlotQueryGenerator forSlot(AbstractContainerScreen<?> screen, Slot slot) {
        var filter = SlotFilter.includedBy(slot);
        return new SlotQueryGenerator(screen, filter);
    }
}
