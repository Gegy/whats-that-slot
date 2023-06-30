package dev.gegy.whats_that_slot.ui;

import net.minecraft.world.item.ItemStack;

public record HoveredItem(ItemStack stack, Bounds2i bounds) {
}
