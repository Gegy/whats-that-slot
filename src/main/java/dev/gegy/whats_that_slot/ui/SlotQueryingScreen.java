package dev.gegy.whats_that_slot.ui;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface SlotQueryingScreen {
    boolean whats_that_slot$mouseScrolled(double amount);

    @Nonnull
    ItemStack whats_that_slot$getHoveredItemAt(double x, double y);
}
