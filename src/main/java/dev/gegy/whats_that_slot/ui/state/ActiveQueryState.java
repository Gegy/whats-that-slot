package dev.gegy.whats_that_slot.ui.state;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.ui.window.SlotQueryWindow;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ActiveQueryState implements SlotQueryState {
    private final ContainerScreen<?> screen;
    private final Slot slot;
    private final SlotQueryWindow window;

    private boolean windowSelected;

    public ActiveQueryState(ContainerScreen<?> screen, Slot slot, SlotQuery query) {
        this.screen = screen;
        this.slot = slot;
        this.window = new SlotQueryWindow(screen, slot, query);
    }

    @Override
    @Nonnull
    public SlotQueryState tick(@Nullable Slot focusedSlot, boolean requestingQuery) {
        if (focusedSlot != this.slot && !this.windowSelected) {
            return new IdleQueryState(this.screen);
        } else {
            return this;
        }
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.window.draw(matrices, mouseX, mouseY);
        this.windowSelected = this.window.isSelected(mouseX, mouseY);
    }

    @Override
    public ActionResultType isSlotSelected(Slot slot, double mouseX, double mouseY) {
        if (this.window.isSelected(mouseX, mouseY)) {
            return this.slot == slot ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        } else {
            return ActionResultType.PASS;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.window.mouseClicked(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.window.mouseDragged(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.window.mouseReleased(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double amount) {
        return this.window.mouseScrolled(amount);
    }

    @Override
    @Nonnull
    public ItemStack getHoveredItemAt(double x, double y) {
        return this.window.getHoveredItemAt(x, y);
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
