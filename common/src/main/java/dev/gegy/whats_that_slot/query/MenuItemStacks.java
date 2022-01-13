package dev.gegy.whats_that_slot.query;

import com.google.common.collect.Iterators;
import dev.gegy.whats_that_slot.mixin.AbstractContainerScreenAccess;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.List;

public record MenuItemStacks(List<Slot> slots) implements Iterable<ItemStack> {
    public static MenuItemStacks of(AbstractContainerScreen<?> screen) {
        var menu = ((AbstractContainerScreenAccess) screen).getMenu();
        return new MenuItemStacks(menu.slots);
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return Iterators.filter(
                Iterators.transform(this.slots.iterator(), Slot::getItem),
                item -> item != null && !item.isEmpty()
        );
    }
}
