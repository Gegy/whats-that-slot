package dev.gegy.whats_that_slot.mixin;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ContainerScreen.class)
public interface AbstractContainerScreenAccess {
    @Invoker("slotClicked")
    void invokeSlotClicked(Slot slot, int slotId, int mouseButton, ClickType clickType);

    @Accessor("menu")
    Container getMenu();

    @Accessor("leftPos")
    int getLeftPos();

    @Accessor("topPos")
    int getTopPos();

    @Accessor("imageWidth")
    int getImageWidth();

    @Accessor("imageHeight")
    int getImageHeight();
}
