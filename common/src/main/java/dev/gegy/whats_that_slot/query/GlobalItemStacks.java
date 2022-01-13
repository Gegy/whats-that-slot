package dev.gegy.whats_that_slot.query;

import com.google.common.collect.AbstractIterator;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public final class GlobalItemStacks implements Iterable<ItemStack> {
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
}
