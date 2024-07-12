package dev.gegy.whats_that_slot.ui.window.texture;

import dev.gegy.whats_that_slot.WhatsThatSlot;
import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.HoveredItem;
import dev.gegy.whats_that_slot.ui.window.SlotGridLayout;
import dev.gegy.whats_that_slot.ui.window.SlotQueryActions;
import dev.gegy.whats_that_slot.ui.window.SlotQueryItems;
import dev.gegy.whats_that_slot.ui.window.SlotQueryPopup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public final class StaticSlotQueryWindow implements SlotQueryWindow {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(WhatsThatSlot.ID, "textures/gui/static_window.png");

    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 128;

    private static final int WIDTH = 102;
    private static final int SLOT_SIZE = SlotGridLayout.SLOT_SIZE;

    private static final int SLOTS_X0 = 7;
    private static final int SLOTS_Y0 = 7;

    private static final int BORDER = 6;

    private final SlotQueryActions actions;
    private final SlotGridLayout grid;
    private final SlotQueryItems items;

    public StaticSlotQueryWindow(SlotQueryActions actions, SlotGridLayout grid, SlotQuery query) {
        this.actions = actions;
        this.grid = grid;

        var slotsBounds = grid.screenBounds(SLOTS_X0, SLOTS_Y0);
        this.items = new SlotQueryItems(slotsBounds, grid, query.items());
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
        this.drawBackground(graphics);

        this.items.drawItems(graphics);
        this.items.drawTooltips(graphics, mouseX, mouseY);
    }

    private void drawBackground(GuiGraphics graphics) {
        graphics.blit(TEXTURE, 0, 0, SlotQueryPopup.BLIT_OFFSET, 0, 0, WIDTH, BORDER, TEXTURE_HEIGHT, TEXTURE_WIDTH);

        for (int i = 0; i < this.grid.countY(); i++) {
            graphics.blit(TEXTURE, 0, BORDER + i * SLOT_SIZE, SlotQueryPopup.BLIT_OFFSET, 0, 6, WIDTH, SLOT_SIZE, TEXTURE_HEIGHT, TEXTURE_WIDTH);
        }

        graphics.blit(TEXTURE, 0, BORDER + this.grid.countY() * SLOT_SIZE, SlotQueryPopup.BLIT_OFFSET, 0, 24, WIDTH, BORDER, TEXTURE_HEIGHT, TEXTURE_WIDTH);
    }

    @Override
    public Bounds2i bounds() {
        return Bounds2i.ofSize(0, 0, WIDTH, BORDER * 2 + this.grid.screenHeight());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY) {
        var queriedItem = this.items.mouseClicked(mouseX, mouseY);
        if (queriedItem != null) {
            return this.actions.selectItem(queriedItem);
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public HoveredItem getHoveredItemAt(double x, double y) {
        return this.items.getHoveredItemAt(x, y);
    }
}
