package dev.gegy.whats_that_slot.query;

import net.minecraft.world.item.ItemStack;

public final class QueriedItem {
    private final ItemStack itemStack;
    private final boolean highlighted;

    QueriedItem(ItemStack itemStack, boolean highlighted) {
        this.itemStack = itemStack;
        this.highlighted = highlighted;
    }

    public static QueriedItem of(ItemStack itemStack) {
        return new QueriedItem(itemStack, false);
    }

    public static QueriedItem ofHighlighted(ItemStack itemStack) {
        return new QueriedItem(itemStack, true);
    }

    public boolean matches(ItemStack target) {
        return ItemStack.isSame(this.itemStack, target) && ItemStack.tagMatches(this.itemStack, target);
    }

    public ItemStack itemStack() {
        return this.itemStack;
    }

    public boolean highlighted() {
        return this.highlighted;
    }
}
