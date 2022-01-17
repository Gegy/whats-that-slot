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
        Iterator<Item> items = Registry.ITEM.iterator();

        return new AbstractIterator<ItemStack>() {
            private final NonNullList<ItemStack> buffer = NonNullList.create();
            private Iterator<ItemStack> currentStacks;

            @Override
            protected ItemStack computeNext() {
                Iterator<ItemStack> currentStacks = this.currentStacks;
                while (currentStacks == null || !currentStacks.hasNext()) {
                    if (!items.hasNext()) return this.endOfData();

                    Item item = items.next();
                    this.currentStacks = currentStacks = this.collectStacksFor(item);
                }

                return currentStacks.next();
            }

            private Iterator<ItemStack> collectStacksFor(Item item) {
                NonNullList<ItemStack> buffer = this.buffer;
                buffer.clear();

                CreativeModeTab category = item.getItemCategory();
                if (category == null) category = CreativeModeTab.TAB_SEARCH;

                item.fillItemCategory(category, buffer);

                return buffer.iterator();
            }
        };
    }
}
