package dev.gegy.whats_that_slot;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class WhatsThatSlotInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        WhatsThatSlot.isDevelopment = FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
