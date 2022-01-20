package dev.gegy.whats_that_slot.ui.window.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

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

    void draw(PoseStack matrices, int mouseX, int mouseY);

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

    @Nonnull
    ItemStack getHoveredItemAt(double x, double y);

    Bounds2i bounds();
}
