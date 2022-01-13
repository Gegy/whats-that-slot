package dev.gegy.whats_that_slot.query;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.function.Predicate;

public final class GlobalItemStacks implements Iterable<ItemStack> {
    private int size = -1;

    public Iterable<ItemStack> filter(Predicate<ItemStack> filter) {
        return Iterables.filter(this, filter::test);
    }

    @Override
    public Iterator<ItemStack> iterator() {
        var items = Registry.ITEM.iterator();

        return new AbstractIterator<>() {
            private final NonNullList<ItemStack> buffer = NonNullList.create();
            private Iterator<ItemStack> currentStacks;

            @Override
            protected ItemStack computeNext() {
                var currentStacks = this.currentStacks;
                while (currentStacks == null || !currentStacks.hasNext()) {
                    if (!items.hasNext()) return this.endOfData();

                    var item = items.next();
                    this.currentStacks = currentStacks = this.collectStacksFor(item);
                }

                return currentStacks.next();
            }

            private Iterator<ItemStack> collectStacksFor(Item item) {
                var buffer = this.buffer;
                buffer.clear();

                var category = item.getItemCategory();
                if (category == null) category = CreativeModeTab.TAB_SEARCH;

                item.fillItemCategory(category, buffer);

                return buffer.iterator();
            }
        };
    }

    public int size() {
        var size = this.size;
        if (size == -1) {
            this.size = size = Iterators.size(this.iterator());
        }
        return size;
    }
}
