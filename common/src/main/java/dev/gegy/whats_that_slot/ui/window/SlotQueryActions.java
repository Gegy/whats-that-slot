package dev.gegy.whats_that_slot.ui.window;

import dev.gegy.whats_that_slot.query.QueriedItem;
import dev.gegy.whats_that_slot.ui.action.ScreenSlotActions;
import dev.gegy.whats_that_slot.ui.action.SlotSelector;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

public interface SlotQueryActions {
    static SlotQueryActions create(AbstractContainerScreen<?> screen, Slot queriedSlot) {
        var slotSelector = SlotSelector.of(screen);

        return item -> {
            var sourceSlot = slotSelector.selectMatching(item);
            if (sourceSlot != null) {
                var actions = new ScreenSlotActions()
                        .swapStacks(sourceSlot, queriedSlot);
                actions.execute(screen);
                return true;
            } else {
                return false;
            }
        };
    }

    boolean selectItem(QueriedItem item);
}
