package dev.gegy.whats_that_slot.ui.window;

import dev.gegy.whats_that_slot.query.QueriedItem;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.HoveredItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class SlotQueryItems {
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

    public void drawItems(GuiGraphics graphics) {
        var player = CLIENT.player;
        var font = CLIENT.font;

        graphics.pose().pushPose();
        graphics.pose().translate(0.0, 0.0, SlotQueryPopup.BLIT_OFFSET);

        this.grid.forEach((index, slotX, slotY) -> {
            var item = this.getItemInSlot(index);
            if (item != null) {
                int screenX = this.screenX(slotX);
                int screenY = this.screenY(slotY);
                this.drawItemSlot(graphics, font, player, item, screenX, screenY);
            }
        });

        graphics.pose().popPose();
    }

    private void drawItemSlot(GuiGraphics graphics, Font font, LocalPlayer player, QueriedItem item, int x, int y) {
        if (item.highlighted()) {
            graphics.fillGradient(x, y, x + 16, y + 16, HIGHLIGHT_COLOR, HIGHLIGHT_COLOR);
        }

        graphics.renderItem(player, item.itemStack(), x, y, 0);
        graphics.renderItemDecorations(font, item.itemStack(), x, y);
    }

    public void drawTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        int focusedSlotX = this.slotX(mouseX);
        int focusedSlotY = this.slotY(mouseY);
        if (this.grid.contains(focusedSlotX, focusedSlotY)) {
            this.drawSlotTooltip(graphics, mouseX, mouseY, focusedSlotX, focusedSlotY);
        }
    }

    private void drawSlotTooltip(GuiGraphics graphics, int mouseX, int mouseY, int slotX, int slotY) {
        int screenX = this.screenX(slotX);
        int screenY = this.screenY(slotY);
        AbstractContainerScreen.renderSlotHighlight(graphics, screenX, screenY, SlotQueryPopup.BLIT_OFFSET);

        var item = this.getItemInSlot(slotX, slotY);
        if (item != null) {
            this.renderItemTooltip(graphics, mouseX, mouseY, item.itemStack());
        }
    }

    private void renderItemTooltip(GuiGraphics graphics, int mouseX, int mouseY, ItemStack item) {
        var screen = CLIENT.screen;
        if (screen != null) {
            var tooltip = Screen.getTooltipFromItem(CLIENT, item);
            var tooltipData = item.getTooltipImage();
            graphics.renderTooltip(CLIENT.font, tooltip, tooltipData, mouseX, mouseY);
        }
    }

    @Nullable
    public QueriedItem mouseClicked(double mouseX, double mouseY) {
        int slotX = this.slotX(Mth.floor(mouseX));
        int slotY = this.slotY(Mth.floor(mouseY));
        return this.getItemInSlot(slotX, slotY);
    }

    @Nullable
    public HoveredItem getHoveredItemAt(double x, double y) {
        int slotX = this.slotX(Mth.floor(x));
        int slotY = this.slotY(Mth.floor(y));

        var item = this.getItemInSlot(slotX, slotY);
        if (item != null) {
            return new HoveredItem(item.itemStack(), this.slotBounds(slotX, slotY));
        } else {
            return null;
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

    private Bounds2i slotBounds(int slotX, int slotY) {
        return new Bounds2i(
                screenX(slotX), screenY(slotY),
                screenX(slotX + 1), screenY(slotY + 1)
        );
    }
}
