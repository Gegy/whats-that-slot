package dev.gegy.whats_that_slot.query;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public record SlotQuery(List<QueriedItem> items) {
    public static SlotQueryGenerator forSlot(AbstractContainerScreen<?> screen, Slot slot) {
        var client = Minecraft.getInstance();
        var customItems = SlotMetadata.getCustomItems(client, screen, slot);
        if (customItems != null) {
            return SlotQueryGenerator.ofCustom(customItems);
        }

        var filter = SlotFilter.includedBy(slot);
        return SlotQueryGenerator.of(screen, filter);
    }
}
