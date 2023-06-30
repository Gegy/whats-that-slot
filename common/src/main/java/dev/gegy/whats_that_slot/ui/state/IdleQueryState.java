package dev.gegy.whats_that_slot.ui.state;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class IdleQueryState implements SlotQueryState {
    private final AbstractContainerScreen<?> screen;

    public IdleQueryState(AbstractContainerScreen<?> screen) {
        this.screen = screen;
    }

    @Override
    @Nonnull
    public SlotQueryState tick(@Nullable Slot focusedSlot, boolean requestingQuery) {
        if (focusedSlot != null && requestingQuery) {
            return new RequestingQueryState(this.screen, focusedSlot);
        } else {
            return this;
        }
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
    }
}
