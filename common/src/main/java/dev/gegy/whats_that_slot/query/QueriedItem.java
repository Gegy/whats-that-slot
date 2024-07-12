package dev.gegy.whats_that_slot.query;

import net.minecraft.world.item.ItemStack;

public record QueriedItem(ItemStack itemStack, boolean highlighted) {
    public static QueriedItem of(ItemStack itemStack) {
        return new QueriedItem(itemStack, false);
    }

    public static QueriedItem ofHighlighted(ItemStack itemStack) {
        return new QueriedItem(itemStack, true);
    }

    public boolean matches(ItemStack target) {
        return ItemStack.isSameItemSameComponents(this.itemStack, target);
    }
}
