package dev.gegy.whats_that_slot.ui.window;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.gegy.whats_that_slot.WhatsThatSlot;
import dev.gegy.whats_that_slot.query.QueriedItem;
import dev.gegy.whats_that_slot.query.SlotQuery;
import dev.gegy.whats_that_slot.ui.Bounds2i;
import dev.gegy.whats_that_slot.ui.action.ScreenSlotActions;
import dev.gegy.whats_that_slot.ui.action.SlotSelector;
import dev.gegy.whats_that_slot.ui.scroll.ScrollView;
import dev.gegy.whats_that_slot.ui.scroll.Scrollbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.List;

// TODO: we can group together stacks from the same item if they all apply
public final class SlotQueryWindow extends AbstractGui {
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

    private final ContainerScreen<?> screen;
    private final SlotSelector slotSelector;

    private final Slot queriedSlot;

    private final SlotGrid slots;
    private final ScrollView scrollView;

    private final Bounds2i bounds;

    private boolean selectedScroller;
    private double scrollerSelectY;

    public SlotQueryWindow(ContainerScreen<?> screen, Slot queriedSlot, SlotQuery query) {
        this.screen = screen;
        this.slotSelector = SlotSelector.of(screen);

        this.queriedSlot = queriedSlot;

        this.slots = new SlotGrid(GRID, query.items());
        this.scrollView = this.slots.createScrollView();

        Bounds2i screenBounds = Bounds2i.ofScreen(screen);
        Bounds2i bounds = Bounds2i.ofSize(
                screenBounds.x0() + queriedSlot.x + 8,
                screenBounds.y0() + queriedSlot.y + 8,
                WIDTH, HEIGHT
        );

        this.bounds = Bounds2i.shiftToFitInScreen(screen, bounds);
    }

    public void draw(MatrixStack matrices, int mouseX, int mouseY) {
        try {
            this.setBlitOffset(200);

            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.bounds.x0(), this.bounds.y0(), this.getBlitOffset());

            Minecraft.getInstance().getTextureManager().bind(TEXTURE);

            this.drawQueryWindow(matrices);

            if (this.scrollView.canScroll()) {
                this.drawScrollBar(matrices);
            }

            if (!this.slots.isEmpty()) {
                this.drawQueryItems(matrices);
                this.drawQueryTooltips(matrices, mouseX - this.bounds.x0(), mouseY - this.bounds.y0());
            }
        } finally {
            RenderSystem.popMatrix();
        }
    }

    public boolean isSelected(double mouseX, double mouseY) {
        return this.bounds.contains(mouseX, mouseY) || this.selectedScroller;
    }

    private void drawQueryWindow(MatrixStack matrices) {
        this.blit(matrices, 0, 0, 0, 0, this.bounds.width(), this.bounds.height());
    }

    private void drawScrollBar(MatrixStack matrices) {
        Bounds2i scroller = this.scrollView.scrollerFromTop(SCROLLBAR, SCROLLER_BOUNDS);

        int v = this.selectedScroller ? SELECTED_SCROLLER_V : SCROLLER_V;
        this.blit(matrices, scroller, SCROLLER_U, v);
    }

    private void drawQueryItems(MatrixStack matrices) {
        ItemRenderer itemRenderer = CLIENT.getItemRenderer();
        ClientPlayerEntity player = CLIENT.player;

        try {
            itemRenderer.blitOffset = this.getBlitOffset();

            GRID.forEach((index, slotX, slotY) -> {
                QueriedItem item = this.slots.get(index);
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

    private void drawItemSlot(MatrixStack matrices, ItemRenderer itemRenderer, ClientPlayerEntity player, QueriedItem item, int x, int y) {
        if (item.highlighted()) {
            this.fillGradient(matrices, x, y, x + 16, y + 16, HIGHLIGHT_COLOR, HIGHLIGHT_COLOR);
        }

        itemRenderer.renderAndDecorateItem(player, item.itemStack(), x, y);
    }

    private void drawQueryTooltips(MatrixStack matrices, int mouseX, int mouseY) {
        int focusedSlotX = GRID.slotX(mouseX);
        int focusedSlotY = GRID.slotY(mouseY);
        if (GRID.contains(focusedSlotX, focusedSlotY)) {
            this.drawSlotTooltip(matrices, mouseX, mouseY, focusedSlotX, focusedSlotY);
        }
    }

    private void drawSlotTooltip(MatrixStack matrices, int mouseX, int mouseY, int slotX, int slotY) {
        int screenX = GRID.screenX(slotX);
        int screenY = GRID.screenY(slotY);

        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        this.fillGradient(matrices, screenX, screenY, screenX + 16, screenY + 16, 0x80FFFFFF, 0x80FFFFFF);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();

        QueriedItem item = this.slots.get(slotX, slotY);
        if (item != null) {
            this.renderItemTooltip(matrices, mouseX, mouseY, item.itemStack());
        }
    }

    private void renderItemTooltip(MatrixStack matrices, int mouseX, int mouseY, ItemStack item) {
        Screen screen = CLIENT.screen;
        if (screen != null) {
            List<ITextComponent> tooltip = screen.getTooltipFromItem(item);
            screen.renderComponentTooltip(matrices, tooltip, mouseX, mouseY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY) {
        if (this.isSelected(mouseX, mouseY)) {
            double windowMouseX = mouseX - this.bounds.x0();
            double windowMouseY = mouseY - this.bounds.y0();

            Bounds2i scroller = this.scrollView.scrollerFromTop(SCROLLBAR, SCROLLER_BOUNDS);
            if (scroller.contains(windowMouseX, windowMouseY) && this.mouseClickedScroller(windowMouseY, scroller)) {
                return true;
            }

            int slotX = GRID.slotX(MathHelper.floor(windowMouseX));
            int slotY = GRID.slotY(MathHelper.floor(windowMouseY));
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
        QueriedItem queriedItem = this.slots.get(slotX, slotY);
        if (queriedItem == null) {
            return false;
        }

        Slot sourceSlot = this.slotSelector.selectMatching(queriedItem);
        if (sourceSlot != null) {
            ScreenSlotActions actions = new ScreenSlotActions()
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

        int scrollerY = MathHelper.floor(windowMouseY - this.scrollerSelectY - SCROLLBAR_Y);
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
        int slotX = GRID.slotX(MathHelper.floor(x - this.bounds.x0()));
        int slotY = GRID.slotY(MathHelper.floor(y - this.bounds.y0()));

        QueriedItem item = this.slots.get(slotX, slotY);
        if (item != null) {
            return item.itemStack();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void blit(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        blit(matrices, x, y, this.getBlitOffset(), u, v, width, height, TEXTURE_HEIGHT, TEXTURE_WIDTH);
    }

    private void blit(MatrixStack matrices, Bounds2i bounds, int u, int v) {
        this.blit(matrices, bounds.x0(), bounds.y0(), u, v, bounds.width(), bounds.height());
    }
}
