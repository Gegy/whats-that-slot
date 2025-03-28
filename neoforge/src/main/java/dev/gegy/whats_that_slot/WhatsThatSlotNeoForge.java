package dev.gegy.whats_that_slot;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("whats_that_slot")
public final class WhatsThatSlotNeoForge {
    public WhatsThatSlotNeoForge() {
        WhatsThatSlot.isDevelopment = !FMLEnvironment.production;
    }
}
