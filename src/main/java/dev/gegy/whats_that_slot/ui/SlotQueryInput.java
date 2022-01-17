package dev.gegy.whats_that_slot.ui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;

public final class SlotQueryInput {
    public static final int REQUEST_TICKS = 20;
    private static final int KEY = GLFW.GLFW_KEY_LEFT_ALT;

    public static boolean isRequestingQuery(Window window) {
        return InputConstants.isKeyDown(window.getWindow(), KEY);
    }
}
