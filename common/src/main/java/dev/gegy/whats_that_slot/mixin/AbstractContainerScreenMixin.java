package dev.gegy.whats_that_slot.mixin;

import dev.gegy.whats_that_slot.ui.HoveredItem;
import dev.gegy.whats_that_slot.ui.SlotQueryInput;
import dev.gegy.whats_that_slot.ui.SlotQueryingScreen;
import dev.gegy.whats_that_slot.ui.state.SlotQueryController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin implements SlotQueryingScreen {
    @Shadow @Nullable protected Slot hoveredSlot;
    @Shadow @Final protected AbstractContainerMenu menu;

    @Unique private final SlotQueryController queryController = new SlotQueryController(
            (AbstractContainerScreen<?>) (Object) this
    );
    @Unique private Slot querySlot;

    @Inject(method = "init()V", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.queryController.reset();
        this.querySlot = null;
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("RETURN"))
    private void captureAndClearFocusedSlot(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.querySlot = this.hoveredSlot;
        if (this.queryController.isActive()) {
            this.hoveredSlot = null;
        }
    }

    @Inject(method = "containerTick()V", at = @At("HEAD"))
    private void tickQuery(CallbackInfo ci) {
        var window = Minecraft.getInstance().getWindow();
        var carriedItem = this.menu.getCarried();
        this.queryController.tick(this.querySlot, SlotQueryInput.isRequestingQuery(window) && carriedItem.isEmpty());
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("HEAD"))
    private void drawQueryDisplay(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci) {
        float delta = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
        this.queryController.draw(graphics, mouseX, mouseY, delta);
    }

    @Inject(method = "isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z", at = @At("HEAD"), cancellable = true)
    private void disableItemHover(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> ci) {
        var result = this.queryController.isSlotSelected(slot, pointX, pointY);
        if (result != InteractionResult.PASS) {
            ci.setReturnValue(result == InteractionResult.SUCCESS);
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
        if (this.queryController.mouseClicked(mouseX, mouseY, button)) {
            ci.setReturnValue(true);
        }
    }

    @Inject(method = "mouseDragged(DDIDD)Z", at = @At("HEAD"), cancellable = true)
    private void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> ci) {
        if (this.queryController.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            ci.setReturnValue(true);
        }
    }

    @Inject(method = "mouseReleased(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
        if (this.queryController.mouseReleased(mouseX, mouseY, button)) {
            ci.setReturnValue(true);
        }
    }

    @Override
    public boolean whats_that_slot$mouseScrolled(double amount) {
        return this.queryController.mouseScrolled(amount);
    }

    @Nullable
    @Override
    public HoveredItem whats_that_slot$getHoveredItemAt(double x, double y) {
        return this.queryController.getHoveredItemAt(x, y);
    }
}
