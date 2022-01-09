package dev.gegy.whats_that_slot.query;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface SlotFilter {
    int ACCEPTS_ALL = 1 << 1;

    static SlotFilter all() {
        return new SlotFilter() {
            @Override
            public boolean accepts(ItemStack stack) {
                return true;
            }

            @Override
            public int characteristics() {
                return ACCEPTS_ALL;
            }
        };
    }

    static SlotFilter includedBy(Slot slot) {
        if (!SlotMetadata.doesOverridePlaceTest(slot)) {
            return SlotFilter.all();
        }

        return stack -> {
            try {
                return slot.mayPlace(stack);
            } catch (Exception e) {
                return false;
            }
        };
    }

    boolean accepts(ItemStack stack);

    default int characteristics() {
        return 0;
    }

    default boolean acceptsAll() {
        return (this.characteristics() & SlotFilter.ACCEPTS_ALL) != 0;
    }
}
