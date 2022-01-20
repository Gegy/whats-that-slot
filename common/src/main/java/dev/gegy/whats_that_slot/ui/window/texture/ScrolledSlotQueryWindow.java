package dev.gegy.whats_that_slot.ui.window.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.gegy.whats_that_slot.WhatsThatSlot;
import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.scroll.ScrollView;
import dev.gegy.whats_that_slot.ui.scroll.Scrollbar;
import dev.gegy.whats_that_slot.ui.window.SlotGridLayout;
import dev.gegy.whats_that_slot.ui.window.SlotQueryActions;
import dev.gegy.whats_that_slot.ui.window.SlotQueryItems;
import dev.gegy.whats_that_slot.ui.window.SlotQueryPopup;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public final class ScrolledSlotQueryWindow extends GuiComponent implements SlotQueryWindow {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WhatsThatSlot.ID, "textures/gui/scrolled_window.png");

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
    private static final int SCROLLER_U = 117;
    private static final int SCROLLER_V = 0;
    private static final int SELECTED_SCROLLER_V = SCROLLER_HEIGHT;

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

        this.setBlitOffset(SlotQueryPopup.BLIT_OFFSET);
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.drawBackground(matrices);
        this.drawScroller(matrices);

        this.items.drawItems(matrices);
        this.items.drawTooltips(matrices, mouseX, mouseY);
    }

    private void drawBackground(PoseStack matrices) {
        this.blit(matrices, 0, 0, 0, 0, WIDTH, HEIGHT);
    }

    private void drawScroller(PoseStack matrices) {
        var scroller = this.scrollView.scrollerFromTop(this.scrollbar, this.scrollerBounds);

        int v = this.selectedScroller ? SELECTED_SCROLLER_V : SCROLLER_V;
        this.blit(matrices, scroller, SCROLLER_U, v);
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

    @Nonnull
    @Override
    public ItemStack getHoveredItemAt(double x, double y) {
        return this.items.getHoveredItemAt(x, y);
    }

    @Override
    public void blit(PoseStack matrices, int x, int y, int u, int v, int width, int height) {
        blit(matrices, x, y, this.getBlitOffset(), u, v, width, height, TEXTURE_HEIGHT, TEXTURE_WIDTH);
    }

    private void blit(PoseStack matrices, Bounds2i bounds, int u, int v) {
        this.blit(matrices, bounds.x0(), bounds.y0(), u, v, bounds.width(), bounds.height());
    }
}
