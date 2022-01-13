package dev.gegy.whats_that_slot.query;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record SlotQuery(
        List<ItemStack> inventoryItems,
        List<ItemStack> globalItems
) {

    public static SlotQueryGenerator forSlot(Player player, Slot slot) {
        var inventory = player.getInventory();
        var filter = SlotFilter.includedBy(slot);
        return new SlotQueryGenerator(inventory, filter);
    }
}
