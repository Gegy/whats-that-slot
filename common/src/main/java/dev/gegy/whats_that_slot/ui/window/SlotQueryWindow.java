package dev.gegy.whats_that_slot.ui.window;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.gegy.whats_that_slot.WhatsThatSlot;
import dev.gegy.whats_that_slot.query.QueriedItem;
import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.action.ScreenSlotActions;
import dev.gegy.whats_that_slot.ui.action.SlotSelector;
import dev.gegy.whats_that_slot.ui.scroll.ScrollView;
import dev.gegy.whats_that_slot.ui.scroll.Scrollbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

// TODO: we can group together stacks from the same item if they all apply
public final class SlotQueryWindow extends GuiComponent {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private static final ResourceLocation TEXTURE = new ResourceLocation(WhatsThatSlot.ID, "textures/gui/window.png");
    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 128;

    private static final int WIDTH = 117;
    private static final int HEIGHT = 102;

    private static final int HIGHLIGHT_COLOR = 0xFF64AA32;

    private static final int SLOTS_X0 = 7;
    private static final int SLOTS_Y0 = 7;
    private static final int SLOTS_COUNT_X = 5;
    private static final int SLOTS_COUNT_Y = 5;

    private static final int SCROLLBAR_X = 100;
    private static final int SCROLLBAR_Y = 7;
    private static final int SCROLLBAR_HEIGHT = 88;

    private static final int SCROLLER_WIDTH = 10;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int SCROLLER_U = 117;
    private static final int SCROLLER_V = 0;
    private static final int SELECTED_SCROLLER_V = SCROLLER_HEIGHT;

    private static final SlotGridLayout GRID = new SlotGridLayout(
            SLOTS_X0, SLOTS_Y0,
            SLOTS_COUNT_X, SLOTS_COUNT_Y
    );

    private static final Scrollbar SCROLLBAR = new Scrollbar(SCROLLBAR_HEIGHT, SCROLLER_HEIGHT);

    private static final Bounds2i SCROLLER_BOUNDS = Bounds2i.ofSize(
            SCROLLBAR_X, SCROLLBAR_Y,
            SCROLLER_WIDTH, SCROLLER_HEIGHT
    );

    private final AbstractContainerScreen<?> screen;
    private final SlotSelector slotSelector;

    private final Slot queriedSlot;

    private final SlotGrid slots;
    private final ScrollView scrollView;

    private final Bounds2i bounds;

    private boolean selectedScroller;
    private double scrollerSelectY;

    public SlotQueryWindow(AbstractContainerScreen<?> screen, Slot queriedSlot, SlotQuery query) {
        this.screen = screen;
        this.slotSelector = SlotSelector.of(screen);

        this.queriedSlot = queriedSlot;

        this.slots = new SlotGrid(GRID, query.items());
        this.scrollView = this.slots.createScrollView();

        var screenBounds = Bounds2i.ofScreen(screen);
        var bounds = Bounds2i.ofSize(
                screenBounds.x0() + queriedSlot.x + 8,
                screenBounds.y0() + queriedSlot.y + 8,
                WIDTH, HEIGHT
        );

        this.bounds = Bounds2i.shiftToFitInScreen(screen, bounds);
    }

    public void draw(PoseStack matrices, int mouseX, int mouseY) {
        var modelViewMatrices = RenderSystem.getModelViewStack();

        try {
            this.setBlitOffset(200);

            modelViewMatrices.pushPose();
            modelViewMatrices.translate(this.bounds.x0(), this.bounds.y0(), this.getBlitOffset());
            RenderSystem.applyModelViewMatrix();

            RenderSystem.setShaderTexture(0, TEXTURE);

            this.drawQueryWindow(matrices);

            if (this.scrollView.canScroll()) {
                this.drawScrollBar(matrices);
            }

            if (!this.slots.isEmpty()) {
                this.drawQueryItems(matrices);
                this.drawQueryTooltips(matrices, mouseX - this.bounds.x0(), mouseY - this.bounds.y0());
            }
        } finally {
            modelViewMatrices.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    public boolean isSelected(double mouseX, double mouseY) {
        return this.bounds.contains(mouseX, mouseY) || this.selectedScroller;
    }

    private void drawQueryWindow(PoseStack matrices) {
        this.blit(matrices, 0, 0, 0, 0, this.bounds.width(), this.bounds.height());
    }

    private void drawScrollBar(PoseStack matrices) {
        var scroller = this.scrollView.scrollerFromTop(SCROLLBAR, SCROLLER_BOUNDS);

        int v = this.selectedScroller ? SELECTED_SCROLLER_V : SCROLLER_V;
        this.blit(matrices, scroller, SCROLLER_U, v);
    }

    private void drawQueryItems(PoseStack matrices) {
        var itemRenderer = CLIENT.getItemRenderer();
        var player = CLIENT.player;

        try {
            itemRenderer.blitOffset = this.getBlitOffset();

            GRID.forEach((index, slotX, slotY) -> {
                var item = this.slots.get(index);
                if (item != null) {
                    int screenX = GRID.screenX(slotX);
                    int screenY = GRID.screenY(slotY);

                    this.drawItemSlot(matrices, itemRenderer, player, item, screenX, screenY);
                }
            });
        } finally {
            itemRenderer.blitOffset = 0.0F;
        }
    }

    private void drawItemSlot(PoseStack matrices, ItemRenderer itemRenderer, LocalPlayer player, QueriedItem item, int x, int y) {
        if (item.highlighted()) {
            this.fillGradient(matrices, x, y, x + 16, y + 16, HIGHLIGHT_COLOR, HIGHLIGHT_COLOR);
        }

        itemRenderer.renderAndDecorateItem(player, item.itemStack(), x, y, 0);
    }

    private void drawQueryTooltips(PoseStack matrices, int mouseX, int mouseY) {
        int focusedSlotX = GRID.slotX(mouseX);
        int focusedSlotY = GRID.slotY(mouseY);
        if (GRID.contains(focusedSlotX, focusedSlotY)) {
            this.drawSlotTooltip(matrices, mouseX, mouseY, focusedSlotX, focusedSlotY);
        }
    }

    private void drawSlotTooltip(PoseStack matrices, int mouseX, int mouseY, int slotX, int slotY) {
        int screenX = GRID.screenX(slotX);
        int screenY = GRID.screenY(slotY);
        AbstractContainerScreen.renderSlotHighlight(matrices, screenX, screenY, this.getBlitOffset());

        var item = this.slots.get(slotX, slotY);
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

    public boolean mouseClicked(double mouseX, double mouseY) {
        if (this.isSelected(mouseX, mouseY)) {
            double windowMouseX = mouseX - this.bounds.x0();
            double windowMouseY = mouseY - this.bounds.y0();

            var scroller = this.scrollView.scrollerFromTop(SCROLLBAR, SCROLLER_BOUNDS);
            if (scroller.contains(windowMouseX, windowMouseY) && this.mouseClickedScroller(windowMouseY, scroller)) {
                return true;
            }

            int slotX = GRID.slotX(Mth.floor(windowMouseX));
            int slotY = GRID.slotY(Mth.floor(windowMouseY));
            if (GRID.contains(slotX, slotY) && this.mouseClickedSlot(slotX, slotY)) {
                return true;
            }

            return true;
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY) {
        if (this.selectedScroller) {
            return this.mouseDraggedScroller(mouseY);
        }

        return this.isSelected(mouseX, mouseY);
    }

    public boolean mouseReleased(double mouseX, double mouseY) {
        if (this.selectedScroller) {
            return this.mouseReleasedScroller();
        }
        return this.isSelected(mouseX, mouseY);
    }

    public boolean mouseScrolled(double amount) {
        this.scrollView.mouseScrolled(amount);
        this.onScrollChanged();
        return true;
    }

    private boolean mouseClickedSlot(int slotX, int slotY) {
        var queriedItem = this.slots.get(slotX, slotY);
        if (queriedItem == null) {
            return false;
        }

        var sourceSlot = this.slotSelector.selectMatching(queriedItem);
        if (sourceSlot != null) {
            var actions = new ScreenSlotActions()
                    .swapStacks(sourceSlot, this.queriedSlot);
            actions.execute(this.screen);

            return true;
        } else {
            return false;
        }
    }

    private boolean mouseClickedScroller(double windowMouseY, Bounds2i scroller) {
        this.selectedScroller = true;
        this.scrollerSelectY = windowMouseY - scroller.y0();
        return true;
    }

    private boolean mouseDraggedScroller(double mouseY) {
        double windowMouseY = mouseY - this.bounds.y0();

        int scrollerY = Mth.floor(windowMouseY - this.scrollerSelectY - SCROLLBAR_Y);
        float scroll = this.scrollView.scrollerToScroll(scrollerY, SCROLLBAR);

        this.scrollView.setScroll(scroll);
        this.onScrollChanged();

        return true;
    }

    private boolean mouseReleasedScroller() {
        this.selectedScroller = false;
        return true;
    }

    private void onScrollChanged() {
        this.slots.applyScroll(this.scrollView.scroll());
    }

    @Nonnull
    public ItemStack getHoveredItemAt(double x, double y) {
        int slotX = GRID.slotX(Mth.floor(x - this.bounds.x0()));
        int slotY = GRID.slotY(Mth.floor(y - this.bounds.y0()));

        var item = this.slots.get(slotX, slotY);
        if (item != null) {
            return item.itemStack();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void blit(PoseStack matrices, int x, int y, int u, int v, int width, int height) {
        blit(matrices, x, y, this.getBlitOffset(), u, v, width, height, TEXTURE_HEIGHT, TEXTURE_WIDTH);
    }

    private void blit(PoseStack matrices, Bounds2i bounds, int u, int v) {
        this.blit(matrices, bounds.x0(), bounds.y0(), u, v, bounds.width(), bounds.height());
    }
}
