package dev.gegy.whats_that_slot.ui.window.texture;

import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.HoveredItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SlotQueryWindow {
    static Bounds2i findPopupBounds(AbstractContainerScreen<?> screen, Bounds2i textureBounds, Slot slot) {
        var screenBounds = Bounds2i.ofScreen(screen);
        var bounds = Bounds2i.ofSize(
                screenBounds.x0() + slot.x + 8,
                screenBounds.y0() + slot.y + 8,
                textureBounds.width(), textureBounds.height()
        );

        return Bounds2i.shiftToFitInScreen(screen, bounds);
    }

    void draw(GuiGraphics graphics, int mouseX, int mouseY);

    default boolean mouseClicked(double mouseX, double mouseY) {
        return false;
    }

    default boolean mouseDragged(double mouseX, double mouseY) {
        return false;
    }

    default boolean mouseReleased(double mouseX, double mouseY) {
        return false;
    }

    default boolean mouseScrolled(double amount) {
        return false;
    }

    @Nullable
    HoveredItem getHoveredItemAt(double x, double y);

    Bounds2i bounds();
}
