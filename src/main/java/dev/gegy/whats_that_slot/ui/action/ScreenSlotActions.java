package dev.gegy.whats_that_slot.ui.action;

import dev.gegy.whats_that_slot.mixin.AbstractContainerScreenAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ScreenSlotActions {
    private final List<Action> actions = new ArrayList<>();

    public ScreenSlotActions swapStacks(Slot sourceSlot, Slot targetSlot) {
        if (sourceSlot == targetSlot) return this;
        if (!targetSlot.mayPlace(sourceSlot.getItem())) return this;

        this.clickSlot(sourceSlot);
        this.clickSlot(targetSlot);

        if (targetSlot.hasItem() && sourceSlot.mayPlace(targetSlot.getItem())) {
            this.clickSlot(sourceSlot);
        }

        return this;
    }

    public ScreenSlotActions clickSlot(Slot slot) {
        this.actions.add((screen, screenAccess) -> {
            screenAccess.invokeSlotClicked(slot, slot.index, GLFW.GLFW_MOUSE_BUTTON_LEFT, ClickType.PICKUP);
        });
        return this;
    }

    public void execute(ContainerScreen<?> screen) {
        Minecraft client = Minecraft.getInstance();
        this.executeNextAction(client, screen, this.actions.iterator());
    }

    private void executeNextAction(Minecraft client, ContainerScreen<?> screen, Iterator<Action> iterator) {
        if (iterator.hasNext()) {
            Action action = iterator.next();
            action.run(screen, (AbstractContainerScreenAccess) screen);

            client.tell(() -> this.executeNextAction(client, screen, iterator));
        }
    }

    public interface Action {
        void run(ContainerScreen<?> screen, AbstractContainerScreenAccess screenAccess);
    }
}
