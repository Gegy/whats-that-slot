package dev.gegy.whats_that_slot.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.gegy.whats_that_slot.ui.SlotQueryInput;
import dev.gegy.whats_that_slot.ui.SlotQueryingScreen;
import dev.gegy.whats_that_slot.ui.state.SlotQueryController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.Slot;
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

    @Unique private final SlotQueryController queryController = new SlotQueryController(
            Minecraft.getInstance().player,
            (AbstractContainerScreen<?>) (Object) this
    );
    @Unique private Slot querySlot;

    @Inject(method = "init()V", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.queryController.reset();
        this.querySlot = null;
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", at = @At("RETURN"))
    private void captureAndClearFocusedSlot(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.querySlot = this.hoveredSlot;
        if (this.queryController.isActive()) {
            this.hoveredSlot = null;
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tickQuery(CallbackInfo ci) {
        var window = Minecraft.getInstance().getWindow();
        this.queryController.tick(this.querySlot, SlotQueryInput.isRequestingQuery(window));
    }

    @Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", at = @At("TAIL"))
    private void drawQueryDisplay(PoseStack matrices, int mouseX, int mouseY, CallbackInfo ci) {
        float delta = Minecraft.getInstance().getDeltaFrameTime();
        this.queryController.draw(matrices, mouseX, mouseY, delta);
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
}
