package dev.gegy.whats_that_slot.query;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;

public final class GlobalItemStacks {
    private GlobalItemStacks() {
    }

    public static Collection<ItemStack> get(Minecraft minecraft) {
        var player = minecraft.player;
        if (player == null) {
            return List.of();
        }

        var hasPermissions = player.canUseGameMasterBlocks() && minecraft.options.operatorItemsTab().get();
        CreativeModeTabs.tryRebuildTabContents(player.level.enabledFeatures(), hasPermissions);

        return CreativeModeTabs.searchTab().getDisplayItems();
    }
}
