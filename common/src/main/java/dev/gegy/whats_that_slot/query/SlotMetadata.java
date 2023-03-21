package dev.gegy.whats_that_slot.query;

import dev.gegy.whats_that_slot.WhatsThatSlot;
import dev.gegy.whats_that_slot.mixin.AbstractContainerScreenAccess;
import dev.gegy.whats_that_slot.mixin.AbstractFurnaceMenuAccess;
import dev.gegy.whats_that_slot.query.recipe.RecipeItemResults;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public final class SlotMetadata {
    private static final Method SUPER_PLACE_TEST;

    private static final WeakHashMap<Class<?>, Boolean> DOES_OVERRIDE_PLACE_TEST = new WeakHashMap<>();

    static {
        var placeTest = findPlaceTestMethod();
        if (placeTest == null) {
            WhatsThatSlot.LOGGER.warn("Unable to find mayPlace method on Slot! Something is very wrong, but we can safely ignore it.");
        }

        SUPER_PLACE_TEST = placeTest;
    }

    @Nullable
    private static Method findPlaceTestMethod() {
        for (var method : Slot.class.getDeclaredMethods()) {
            var parameters = method.getParameterTypes();
            var returnType = method.getReturnType();
            if (parameters.length == 1 && parameters[0] == ItemStack.class && returnType == boolean.class) {
                return method;
            }
        }
        return null;
    }

    public static boolean doesOverridePlaceTest(Slot slot) {
        return DOES_OVERRIDE_PLACE_TEST.computeIfAbsent(slot.getClass(), SlotMetadata::doesOverridePlaceTest);
    }

    private static boolean doesOverridePlaceTest(Class<?> clazz) {
        var superPlaceTest = SUPER_PLACE_TEST;
        if (superPlaceTest != null) {
            return doesOverride(clazz, superPlaceTest);
        } else {
            return false;
        }
    }

    private static boolean doesOverride(Class<?> clazz, Method superMethod) {
        try {
            var overridingMethod = clazz.getMethod(superMethod.getName(), superMethod.getParameterTypes());
            return overridingMethod.getDeclaringClass() != Slot.class;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    // TODO: expose api?
    @Nullable
    public static Iterable<ItemStack> getCustomItems(Minecraft client, AbstractContainerScreen<?> screen, Slot slot) {
        if (client.level != null) {
            var registryAccess = client.level.registryAccess();
            var recipeManager = client.level.getRecipeManager();
            if (slot instanceof ResultSlot) {
                return new RecipeItemResults(registryAccess, recipeManager, RecipeType.CRAFTING);
            } else if (slot instanceof FurnaceResultSlot) {
                var menu = ((AbstractContainerScreenAccess) screen).getMenu();
                if (menu instanceof AbstractFurnaceMenuAccess furnace) {
                    var recipeType = furnace.getRecipeType();
                    return new RecipeItemResults(registryAccess, recipeManager, recipeType);
                }
            }
        }

        return null;
    }
}
