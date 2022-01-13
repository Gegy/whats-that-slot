package dev.gegy.whats_that_slot.query;

import net.minecraft.world.item.ItemStack;

public record QueriedItem(ItemStack itemStack, boolean highlighted) {
    public static QueriedItem of(ItemStack itemStack) {
        return new QueriedItem(itemStack, false);
    }

    public static QueriedItem ofHighlighted(ItemStack itemStack) {
        return new QueriedItem(itemStack, true);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        return obj instanceof QueriedItem item && ItemStack.matches(this.itemStack, item.itemStack);
    }
}
