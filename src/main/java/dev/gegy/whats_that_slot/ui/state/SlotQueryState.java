package dev.gegy.whats_that_slot.ui.state;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SlotQueryState {
    @Nonnull
    SlotQueryState tick(@Nullable Slot focusedSlot, boolean requestingQuery);

    void draw(MatrixStack matrices, int mouseX, int mouseY, float delta);

    default ActionResultType isSlotSelected(Slot slot, double mouseX, double mouseY) {
        return ActionResultType.PASS;
    }

    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseScrolled(double amount) {
        return false;
    }

    @Nonnull
    default ItemStack getHoveredItemAt(double x, double y) {
        return ItemStack.EMPTY;
    }

    default boolean isActive() {
        return false;
    }
}
