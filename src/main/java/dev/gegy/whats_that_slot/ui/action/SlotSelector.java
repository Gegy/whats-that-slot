package dev.gegy.whats_that_slot.ui.action;

import dev.gegy.whats_that_slot.mixin.AbstractContainerScreenAccess;
import dev.gegy.whats_that_slot.query.QueriedItem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SlotSelector {
    private final List<Slot> slots;

    private SlotSelector(List<Slot> slots) {
        this.slots = slots;
    }

    public static SlotSelector of(AbstractContainerMenu menu) {
        List<Slot> sortedSlots = new ArrayList<>(menu.slots);
        sortedSlots.sort(Comparator.comparingInt(slot -> isPlayerSlot(slot) ? 0 : 1));
        return new SlotSelector(sortedSlots);
    }

    public static SlotSelector of(AbstractContainerScreen<?> screen) {
        AbstractContainerMenu menu = ((AbstractContainerScreenAccess) screen).getMenu();
        return SlotSelector.of(menu);
    }

    @Nullable
    public Slot selectMatching(QueriedItem queriedItem) {
        for (Slot slot : this.slots) {
            if (queriedItem.matches(slot.getItem())) {
                return slot;
            }
        }
        return null;
    }

    private static boolean isPlayerSlot(Slot slot) {
        return slot.container instanceof Inventory;
    }

    public List<Slot> slots() {
        return this.slots;
    }
}
