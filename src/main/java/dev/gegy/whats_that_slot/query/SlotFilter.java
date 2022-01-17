package dev.gegy.whats_that_slot.query;

import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public final class SlotFilter {
    public static Predicate<ItemStack> includedBy(Slot slot) {
        // TODO: this is not used to a significant extent- it may be worth removing
        if (!SlotMetadata.doesOverridePlaceTest(slot)) {
            return item -> true;
        }

        return stack -> {
            try {
                return slot.mayPlace(stack);
            } catch (Exception e) {
                return false;
            }
        };
    }
}
