package dev.gegy.whats_that_slot.query;

import com.google.common.collect.Iterators;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public record InventoryItemStacks(Inventory inventory) implements Iterable<ItemStack> {
    public static Iterator<ItemStack> iterator(Inventory inventory) {
        return Iterators.filter(
                Iterators.concat(
                        inventory.items.iterator(),
                        inventory.armor.iterator(),
                        inventory.offhand.iterator()
                ),
                item -> item != null && !item.isEmpty()
        );
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return InventoryItemStacks.iterator(this.inventory);
    }
}
