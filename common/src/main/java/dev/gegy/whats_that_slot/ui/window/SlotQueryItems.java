package dev.gegy.whats_that_slot.ui.window;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.gegy.whats_that_slot.query.QueriedItem;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class SlotQueryItems extends GuiComponent {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private static final int HIGHLIGHT_COLOR = 0xFF64AA32;

    private final Bounds2i bounds;
    private final SlotGridLayout grid;

    private final List<QueriedItem> items;
    private int scrollIndexOffset;

    public SlotQueryItems(Bounds2i bounds, SlotGridLayout grid, List<QueriedItem> items) {
        this.bounds = bounds;
        this.grid = grid;
        this.items = items;
    }

    public void applyScroll(float scroll) {
        this.scrollIndexOffset = Mth.floor(scroll) * this.grid.countX();
    }

    public void drawItems(PoseStack matrices) {
        var itemRenderer = CLIENT.getItemRenderer();
        var player = CLIENT.player;

        matrices.pushPose();
        matrices.translate(0.0, 0.0, SlotQueryPopup.BLIT_OFFSET);

        this.grid.forEach((index, slotX, slotY) -> {
            var item = this.getItemInSlot(index);
            if (item != null) {
                int screenX = this.screenX(slotX);
                int screenY = this.screenY(slotY);
                this.drawItemSlot(matrices, itemRenderer, player, item, screenX, screenY);
            }
        });

        matrices.popPose();
    }

    private void drawItemSlot(PoseStack matrices, ItemRenderer itemRenderer, LocalPlayer player, QueriedItem item, int x, int y) {
        if (item.highlighted()) {
            fillGradient(matrices, x, y, x + 16, y + 16, HIGHLIGHT_COLOR, HIGHLIGHT_COLOR);
        }

        itemRenderer.renderAndDecorateItem(matrices, player, item.itemStack(), x, y, 0);
    }

    public void drawTooltips(PoseStack matrices, int mouseX, int mouseY) {
        int focusedSlotX = this.slotX(mouseX);
        int focusedSlotY = this.slotY(mouseY);
        if (this.grid.contains(focusedSlotX, focusedSlotY)) {
            this.drawSlotTooltip(matrices, mouseX, mouseY, focusedSlotX, focusedSlotY);
        }
    }

    private void drawSlotTooltip(PoseStack matrices, int mouseX, int mouseY, int slotX, int slotY) {
        int screenX = this.screenX(slotX);
        int screenY = this.screenY(slotY);
        AbstractContainerScreen.renderSlotHighlight(matrices, screenX, screenY, SlotQueryPopup.BLIT_OFFSET);

        var item = this.getItemInSlot(slotX, slotY);
        if (item != null) {
            this.renderItemTooltip(matrices, mouseX, mouseY, item.itemStack());
        }
    }

    private void renderItemTooltip(PoseStack matrices, int mouseX, int mouseY, ItemStack item) {
        var screen = CLIENT.screen;
        if (screen != null) {
            var tooltip = screen.getTooltipFromItem(item);
            var tooltipData = item.getTooltipImage();
            screen.renderTooltip(matrices, tooltip, tooltipData, mouseX, mouseY);
        }
    }

    @Nullable
    public QueriedItem mouseClicked(double mouseX, double mouseY) {
        int slotX = this.slotX(Mth.floor(mouseX));
        int slotY = this.slotY(Mth.floor(mouseY));
        return this.getItemInSlot(slotX, slotY);
    }

    @Nonnull
    public ItemStack getHoveredItemAt(double x, double y) {
        int slotX = this.slotX(Mth.floor(x));
        int slotY = this.slotY(Mth.floor(y));

        var item = this.getItemInSlot(slotX, slotY);
        if (item != null) {
            return item.itemStack();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Nullable
    private QueriedItem getItemInSlot(int x, int y) {
        if (this.grid.contains(x, y)) {
            return this.getItemInSlot(this.grid.index(x, y));
        } else {
            return null;
        }
    }

    @Nullable
    private QueriedItem getItemInSlot(int index) {
        int listIndex = index + this.scrollIndexOffset;
        if (listIndex >= 0 && listIndex < this.items.size()) {
            return this.items.get(listIndex);
        } else {
            return null;
        }
    }

    private int slotX(int mouseX) {
        return this.grid.slotX(mouseX - this.bounds.x0());
    }

    private int slotY(int mouseY) {
        return this.grid.slotY(mouseY - this.bounds.y0());
    }

    private int screenX(int slotX) {
        return this.grid.screenX(slotX) + this.bounds.x0();
    }

    private int screenY(int slotY) {
        return this.grid.screenY(slotY) + this.bounds.y0();
    }
}
