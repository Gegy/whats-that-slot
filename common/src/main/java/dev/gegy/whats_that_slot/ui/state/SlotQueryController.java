package dev.gegy.whats_that_slot.ui.state;

import dev.gegy.whats_that_slot.ui.HoveredItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.Slot;

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

    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.state.draw(graphics, mouseX, mouseY, delta);
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

    @Nullable
    public HoveredItem getHoveredItemAt(double x, double y) {
        return this.state.getHoveredItemAt(x, y);
    }

    public boolean isActive() {
        return this.state.isActive();
    }
}
