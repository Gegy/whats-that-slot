package dev.gegy.whats_that_slot.ui.window.texture;

import dev.gegy.whats_that_slot.WhatsThatSlot;
import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.HoveredItem;
import dev.gegy.whats_that_slot.ui.scroll.ScrollView;
import dev.gegy.whats_that_slot.ui.scroll.Scrollbar;
import dev.gegy.whats_that_slot.ui.window.SlotGridLayout;
import dev.gegy.whats_that_slot.ui.window.SlotQueryActions;
import dev.gegy.whats_that_slot.ui.window.SlotQueryItems;
import dev.gegy.whats_that_slot.ui.window.SlotQueryPopup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public final class ScrolledSlotQueryWindow implements SlotQueryWindow {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(WhatsThatSlot.ID, "textures/gui/scrolled_window.png");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    private static final ResourceLocation SCROLLER_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");

    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 128;

    private static final int WIDTH = 117;
    private static final int HEIGHT = 102;

    private static final int SLOTS_X0 = 7;
    private static final int SLOTS_Y0 = 7;

    private static final int SCROLLBAR_X = 100;
    private static final int SCROLLBAR_Y = 7;

    public static final int SCROLLER_WIDTH = 10;
    public static final int SCROLLER_HEIGHT = 15;

    private final SlotQueryActions actions;
    private final SlotQueryItems items;

    private final ScrollView scrollView;

    private final Scrollbar scrollbar;
    private final Bounds2i scrollerBounds;

    private double scrollerSelectY;
    private boolean selectedScroller;

    public ScrolledSlotQueryWindow(SlotQueryActions actions, SlotGridLayout grid, SlotQuery query, ScrollView scrollView) {
        this.actions = actions;
        this.items = new SlotQueryItems(grid.screenBounds(SLOTS_X0, SLOTS_Y0), grid, query.items());
        this.scrollView = scrollView;

        int scrollbarHeight = grid.countY() * SlotGridLayout.SLOT_SIZE - 2;
        this.scrollbar = new Scrollbar(scrollbarHeight, SCROLLER_HEIGHT);
        this.scrollerBounds = Bounds2i.ofSize(
                SCROLLBAR_X, SCROLLBAR_Y,
                SCROLLER_WIDTH, SCROLLER_HEIGHT
        );
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
        this.drawBackground(graphics);
        this.drawScroller(graphics);

        this.items.drawItems(graphics);
        this.items.drawTooltips(graphics, mouseX, mouseY);
    }

    private void drawBackground(GuiGraphics graphics) {
        graphics.blit(TEXTURE, 0, 0, SlotQueryPopup.BLIT_OFFSET, 0, 0, WIDTH, HEIGHT, TEXTURE_HEIGHT, TEXTURE_WIDTH);
    }

    private void drawScroller(GuiGraphics graphics) {
        var scroller = this.scrollView.scrollerFromTop(this.scrollbar, this.scrollerBounds);

        this.blitSprite(graphics, scroller, this.selectedScroller ? SCROLLER_SELECTED_SPRITE : SCROLLER_SPRITE);
    }

    @Override
    public Bounds2i bounds() {
        return Bounds2i.ofSize(0, 0, WIDTH, HEIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY) {
        var scroller = this.scrollView.scrollerFromTop(this.scrollbar, this.scrollerBounds);
        if (scroller.contains(mouseX, mouseY)) {
            this.mouseClickedScroller(mouseY, scroller);
            return true;
        }

        var queriedItem = this.items.mouseClicked(mouseX, mouseY);
        if (queriedItem != null) {
            return this.actions.selectItem(queriedItem);
        }

        return false;
    }

    private void mouseClickedScroller(double mouseY, Bounds2i scroller) {
        this.selectedScroller = true;
        this.scrollerSelectY = mouseY - scroller.y0();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY) {
        if (this.selectedScroller) {
            int scrollerY = Mth.floor(mouseY - this.scrollerSelectY - SCROLLBAR_Y);
            float scroll = this.scrollView.scrollerToScroll(scrollerY, this.scrollbar);

            this.scrollView.setScroll(scroll);
            this.onScrollChanged();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        if (this.selectedScroller) {
            this.selectedScroller = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseScrolled(double amount) {
        this.scrollView.mouseScrolled(amount);
        this.onScrollChanged();
        return true;
    }

    private void onScrollChanged() {
        this.items.applyScroll(this.scrollView.scroll());
    }

    @Nullable
    @Override
    public HoveredItem getHoveredItemAt(double x, double y) {
        return this.items.getHoveredItemAt(x, y);
    }

    private void blitSprite(GuiGraphics graphics, Bounds2i bounds, ResourceLocation sprite) {
        graphics.blitSprite(sprite, bounds.x0(), bounds.y0(), SlotQueryPopup.BLIT_OFFSET, bounds.width(), bounds.height());
    }
}
