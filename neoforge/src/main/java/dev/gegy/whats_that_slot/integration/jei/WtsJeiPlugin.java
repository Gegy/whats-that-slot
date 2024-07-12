package dev.gegy.whats_that_slot.integration.jei;

import dev.gegy.whats_that_slot.WhatsThatSlot;
import dev.gegy.whats_that_slot.ui.SlotQueryingScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

@JeiPlugin
public final class WtsJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(WhatsThatSlot.ID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(AbstractContainerScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(AbstractContainerScreen screen, double mouseX, double mouseY) {
                if (screen instanceof SlotQueryingScreen queryingScreen) {
                    var item = queryingScreen.whats_that_slot$getHoveredItemAt(mouseX, mouseY);
                    if (item != null) {
                        var ingredientManager = registration.getJeiHelpers().getIngredientManager();
                        return ingredientManager.createTypedIngredient(VanillaTypes.ITEM_STACK, item.stack()).map(ingredient -> new IClickableIngredient<ItemStack>() {
                            @Override
                            public ITypedIngredient<ItemStack> getTypedIngredient() {
                                return ingredient;
                            }

                            @Override
                            public Rect2i getArea() {
                                return item.bounds().toRect();
                            }
                        });
                    }
                }
                return Optional.empty();
            }
        });
    }
}
