package dev.gegy.whats_that_slot.ui.state;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class IdleQueryState implements SlotQueryState {
    private final ContainerScreen<?> screen;

    public IdleQueryState(ContainerScreen<?> screen) {
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
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }
}
