package dev.gegy.whats_that_slot.query;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public final class ItemStackLinkedSet extends ObjectLinkedOpenCustomHashSet<ItemStack> {
    private static final Hash.Strategy<? super ItemStack> STRATEGY = new Hash.Strategy<>() {
        @Override
        public int hashCode(ItemStack item) {
            if (item != null) {
                return Objects.hash(item.getItem(), item.getTag());
            } else {
                return 0;
            }
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            return a == b || (a != null && b != null && ItemStack.matches(a, b));
        }
    };

    public ItemStackLinkedSet() {
        super(STRATEGY);
    }
}
