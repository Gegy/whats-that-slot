package dev.gegy.whats_that_slot.ui.state;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SlotQueryController {
    private final AbstractContainerScreen<?> screen;

    private SlotQueryState state;

    public SlotQueryController(AbstractContainerScreen<?> screen) {
        this.screen = screen;
        this.reset();
    }

    public void reset() {
        this.state = new IdleQueryState(this.screen);
    }

    public void tick(@Nullable Slot hoveredSlot, boolean requestingQuery) {
        this.state = this.state.tick(hoveredSlot, requestingQuery);
    }

    public void draw(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.state.draw(matrices, mouseX, mouseY, delta);
    }

    public InteractionResult isSlotSelected(Slot slot, double mouseX, double mouseY) {
        return this.state.isSlotSelected(slot, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.state.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.state.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.state.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double amount) {
        return this.state.mouseScrolled(amount);
    }

    @Nonnull
    public ItemStack getHoveredItemAt(double x, double y) {
        return this.state.getHoveredItemAt(x, y);
    }

    public boolean isActive() {
        return this.state.isActive();
    }
}
