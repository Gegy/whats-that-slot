package dev.gegy.whats_that_slot.integration.rei;

import dev.architectury.event.CompoundEventResult;
import dev.gegy.whats_that_slot.ui.SlotQueryingScreen;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public final class WtsReiClientPlugin implements REIClientPlugin {
    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerFocusedStack((screen, mouse) -> {
            if (screen instanceof SlotQueryingScreen queryingScreen) {
                var item = queryingScreen.whats_that_slot$getHoveredItemAt(mouse.x, mouse.y);
                if (item != null) {
                    return CompoundEventResult.interruptTrue(EntryStacks.of(item.stack()));
                }
            }
            return CompoundEventResult.pass();
        });
    }
}
