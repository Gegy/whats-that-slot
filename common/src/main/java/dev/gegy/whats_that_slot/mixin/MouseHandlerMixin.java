package dev.gegy.whats_that_slot.mixin;

import dev.gegy.whats_that_slot.ui.SlotQueryingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "onScroll(JDD)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z", shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void onMouseScroll(
            long window, double horizontal, double vertical, CallbackInfo ci,
            double amount, double mouseX, double mouseY
    ) {
        if (this.minecraft.screen instanceof SlotQueryingScreen screen) {
            if (screen.whats_that_slot$mouseScrolled(amount)) {
                ci.cancel();
            }
        }
    }
}
