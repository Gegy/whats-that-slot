package dev.gegy.whats_that_slot.query;

import com.google.common.collect.Iterators;
import dev.gegy.whats_that_slot.mixin.AbstractContainerScreenAccess;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.List;

public final class MenuItemStacks implements Iterable<ItemStack> {
    private final List<Slot> slots;

    public MenuItemStacks(List<Slot> slots) {
        this.slots = slots;
    }

    public static MenuItemStacks of(ContainerScreen<?> screen) {
        Container menu = ((AbstractContainerScreenAccess) screen).getMenu();
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
