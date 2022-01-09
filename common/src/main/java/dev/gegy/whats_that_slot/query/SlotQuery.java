package dev.gegy.whats_that_slot.query;

import dev.gegy.whats_that_slot.WhatsThatSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;

// TODO: sort & highlight items that the player already has in their inventory
public final class SlotQuery {
    private final List<ItemStack> results;

    private SlotQuery(List<ItemStack> results) {
        this.results = results;
    }

    public static Generator forSlot(Slot slot) {
        return new Generator(slot);
    }

    public List<ItemStack> getResults() {
        return this.results;
    }

    // over-engineered? why yes of course
    public static final class Generator {
        private final GlobalItemStacks stacks = WhatsThatSlot.GLOBAL_STACKS;
        private final Iterator<ItemStack> stackIterator = this.stacks.iterator();

        private final SlotFilter filter;

        private int matchCount;

        private boolean complete;

        private Generator(Slot slot) {
            this.filter = SlotFilter.includedBy(slot);

            if (this.filter.acceptsAll()) {
                this.matchCount = this.stacks.size();
                this.complete = true;
            }
        }

        public void advanceFor(Duration duration) {
            if (!this.complete) {
                long targetTime = System.nanoTime() + duration.toNanos();
                this.advance(() -> System.nanoTime() < targetTime);
            }
        }

        private void advance(BooleanSupplier shouldContinue) {
            while (shouldContinue.getAsBoolean() && !this.complete) {
                if (!this.stackIterator.hasNext()) {
                    this.complete = true;
                    break;
                }

                var stack = this.stackIterator.next();
                if (this.filter.accepts(stack)) {
                    this.matchCount++;
                }
            }
        }

        public SlotQuery build() {
            this.advance(() -> true);
            return new SlotQuery(LazyFillingList.ofIterable(this.stacks.filter(this.filter), this.matchCount));
        }
    }
}
