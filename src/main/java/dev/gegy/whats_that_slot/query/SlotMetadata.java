package dev.gegy.whats_that_slot.query;

import dev.gegy.whats_that_slot.WhatsThatSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public final class SlotMetadata {
    private static final Method SUPER_PLACE_TEST;

    private static final WeakHashMap<Class<?>, Boolean> DOES_OVERRIDE_PLACE_TEST = new WeakHashMap<>();

    static {
        Method placeTest = findPlaceTestMethod();
        if (placeTest == null) {
            WhatsThatSlot.LOGGER.warn("Unable to find mayPlace method on Slot! Something is very wrong, but we can safely ignore it.");
        }

        SUPER_PLACE_TEST = placeTest;
    }

    @Nullable
    private static Method findPlaceTestMethod() {
        for (Method method : Slot.class.getDeclaredMethods()) {
            Class<?>[] parameters = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();
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
        Method superPlaceTest = SUPER_PLACE_TEST;
        if (superPlaceTest != null) {
            return doesOverride(clazz, superPlaceTest);
        } else {
            return false;
        }
    }

    private static boolean doesOverride(Class<?> clazz, Method superMethod) {
        try {
            Method overridingMethod = clazz.getMethod(superMethod.getName(), superMethod.getParameterTypes());
            return overridingMethod.getDeclaringClass() != Slot.class;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
