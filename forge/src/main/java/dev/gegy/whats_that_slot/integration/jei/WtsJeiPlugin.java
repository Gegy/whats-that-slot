/*
package dev.gegy.whats_that_slot.integration.jei;

import dev.gegy.whats_that_slot.WhatsThatSlot;
import dev.gegy.whats_that_slot.ui.SlotQueryingScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public final class WtsJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(WhatsThatSlot.ID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(AbstractContainerScreen.class, new IGuiContainerHandler<>() {
            @Nullable
            @Override
            public Object getIngredientUnderMouse(AbstractContainerScreen screen, double mouseX, double mouseY) {
                if (screen instanceof SlotQueryingScreen queryingScreen) {
                    var item = queryingScreen.whats_that_slot$getHoveredItemAt(mouseX, mouseY);
                    if (!item.isEmpty()) {
                        return item;
                    }
                }
                return null;
            }
        });
    }
}
*/
