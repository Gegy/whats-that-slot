package dev.gegy.whats_that_slot.ui.window;

import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.HoveredItem;
import dev.gegy.whats_that_slot.ui.window.texture.ScrolledSlotQueryWindow;
import dev.gegy.whats_that_slot.ui.window.texture.SlotQueryWindow;
import dev.gegy.whats_that_slot.ui.window.texture.StaticSlotQueryWindow;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO: we can group together stacks from the same item if they all apply
public final class SlotQueryPopup {
    public static final int BLIT_OFFSET = 200;

    public static final int SLOTS_COUNT_X = 5;
    public static final int MAX_SLOTS_COUNT_Y = 5;

    private final SlotQueryWindow window;

    private final Bounds2i bounds;

    private boolean selected;

    public SlotQueryPopup(AbstractContainerScreen<?> screen, Slot queriedSlot, SlotQuery query) {
        var actions = SlotQueryActions.create(screen, queriedSlot);

        this.window = this.createWindow(actions, query);
        this.bounds = SlotQueryWindow.findPopupBounds(screen, this.window.bounds(), queriedSlot);
    }

    private SlotQueryWindow createWindow(SlotQueryActions actions, SlotQuery query) {
        int itemCount = query.items().size();
        var grid = SlotGridLayout.createVertical(SLOTS_COUNT_X, MAX_SLOTS_COUNT_Y, itemCount);

        var scrollView = grid.createScrollView(itemCount);
        if (scrollView.canScroll()) {
            return new ScrolledSlotQueryWindow(actions, grid, query, scrollView);
        } else {
            return new StaticSlotQueryWindow(actions, grid, query);
        }
    }

    public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.pose().pushPose();
        graphics.pose().translate(this.bounds.x0(), this.bounds.y0(), BLIT_OFFSET);

        this.window.draw(graphics, mouseX - this.bounds.x0(), mouseY - this.bounds.y0());
        graphics.pose().popPose();
    }

    public boolean isSelected(double mouseX, double mouseY) {
        return this.bounds.contains(mouseX, mouseY) || this.selected;
    }

    public boolean mouseClicked(double mouseX, double mouseY) {
        if (this.isSelected(mouseX, mouseY)) {
            if (this.window.mouseClicked(mouseX - this.bounds.x0(), mouseY - this.bounds.y0())) {
                this.selected = true;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY) {
        if (this.selected) {
            this.window.mouseDragged(mouseX - this.bounds.x0(), mouseY - this.bounds.y0());
            return true;
        }
        return this.isSelected(mouseX, mouseY);
    }

    public boolean mouseReleased(double mouseX, double mouseY) {
        if (this.selected) {
            this.selected = false;
            this.window.mouseReleased(mouseX - this.bounds.x0(), mouseY - this.bounds.y0());
            return true;
        }
        return this.isSelected(mouseX, mouseY);
    }

    public boolean mouseScrolled(double amount) {
        this.window.mouseScrolled(amount);
        return true;
    }

    @Nullable
    public HoveredItem getHoveredItemAt(double x, double y) {
        var hovered = this.window.getHoveredItemAt(x - this.bounds.x0(), y - this.bounds.y0());
        if (hovered != null) {
            return new HoveredItem(hovered.stack(), hovered.bounds().offset(this.bounds.x0(), this.bounds.y0()));
        }
        return null;
    }
}
