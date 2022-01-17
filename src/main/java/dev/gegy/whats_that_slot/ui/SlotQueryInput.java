package dev.gegy.whats_that_slot.ui;

import net.minecraft.client.MainWindow;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

public final class SlotQueryInput {
    public static final int REQUEST_TICKS = 20;
    private static final int KEY = GLFW.GLFW_KEY_LEFT_ALT;

    public static boolean isRequestingQuery(MainWindow window) {
        return InputMappings.isKeyDown(window.getWindow(), KEY);
    }
}
