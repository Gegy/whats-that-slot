package dev.gegy.whats_that_slot.ui;

import javax.annotation.Nullable;

public interface SlotQueryingScreen {
    boolean whats_that_slot$mouseScrolled(double amount);

    @Nullable
    HoveredItem whats_that_slot$getHoveredItemAt(double x, double y);
}
