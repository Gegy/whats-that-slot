package dev.gegy.whats_that_slot;

import dev.gegy.whats_that_slot.query.GlobalItemStacks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WhatsThatSlot {
    public static final String ID = "whats_that_slot";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GlobalItemStacks GLOBAL_STACKS = new GlobalItemStacks();
}
