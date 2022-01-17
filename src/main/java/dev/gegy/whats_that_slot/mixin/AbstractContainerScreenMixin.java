package dev.gegy.whats_that_slot.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.gegy.whats_that_slot.ui.SlotQueryInput;
import dev.gegy.whats_that_slot.ui.SlotQueryingScreen;
import dev.gegy.whats_that_slot.ui.state.SlotQueryController;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mixin(ContainerScreen.class)
public class AbstractContainerScreenMixin implements SlotQueryingScreen {
    @Shadow @Nullable protected Slot hoveredSlot;

    @Unique private final SlotQueryController queryController = new SlotQueryController(
            (ContainerScreen<?>) (Object) this
    );
    @Unique private Slot querySlot;

    @Inject(method = "init()V", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.queryController.reset();
        this.querySlot = null;
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V", at = @At("RETURN"))
    private void captureAndClearFocusedSlot(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.querySlot = this.hoveredSlot;
        if (this.queryController.isActive()) {
            this.hoveredSlot = null;
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tickQuery(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        MainWindow window = client.getWindow();
        ItemStack carriedItem = client.player.inventory.getCarried();
        this.queryController.tick(this.querySlot, SlotQueryInput.isRequestingQuery(window) && carriedItem.isEmpty());
    }

    @Inject(method = "renderTooltip(Lcom/mojang/blaze3d/matrix/MatrixStack;II)V", at = @At("TAIL"))
    private void drawQueryDisplay(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci) {
        float delta = Minecraft.getInstance().getDeltaFrameTime();
        this.queryController.draw(matrices, mouseX, mouseY, delta);
    }

    @Inject(method = "isHovering(Lnet/minecraft/inventory/container/Slot;DD)Z", at = @At("HEAD"), cancellable = true)
    private void disableItemHover(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> ci) {
        ActionResultType result = this.queryController.isSlotSelected(slot, pointX, pointY);
        if (result != ActionResultType.PASS) {
            ci.setReturnValue(result == ActionResultType.SUCCESS);
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

    @Nonnull
    @Override
    public ItemStack whats_that_slot$getHoveredItemAt(double x, double y) {
        return this.queryController.getHoveredItemAt(x, y);
    }
}
