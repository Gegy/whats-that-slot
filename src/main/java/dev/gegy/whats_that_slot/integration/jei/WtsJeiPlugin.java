package dev.gegy.whats_that_slot.integration.jei;

import dev.gegy.whats_that_slot.WhatsThatSlot;
import dev.gegy.whats_that_slot.ui.SlotQueryingScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public final class WtsJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(WhatsThatSlot.ID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        Class<ContainerScreen<?>> containerScreenClass = (Class) ContainerScreen.class;
        registration.addGuiContainerHandler(containerScreenClass, new IGuiContainerHandler<ContainerScreen<?>>() {
            @Override
            public Object getIngredientUnderMouse(ContainerScreen<?> screen, double mouseX, double mouseY) {
                if (screen instanceof SlotQueryingScreen) {
                    ItemStack item = ((SlotQueryingScreen) screen).whats_that_slot$getHoveredItemAt(mouseX, mouseY);
                    if (!item.isEmpty()) {
                        return item;
                    }
                }
                return null;
            }
        });
    }
}
