package dev.gegy.whats_that_slot.query;

import com.google.common.collect.Iterators;
import dev.gegy.whats_that_slot.collection.ConcatList;
import dev.gegy.whats_that_slot.collection.LazyFillingList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public final class SlotQueryGenerator {
    private State state;
    private SlotQuery results;

    private SlotQueryGenerator(State state) {
        this.state = state;
    }

    public static SlotQueryGenerator of(AbstractContainerScreen<?> screen, Predicate<ItemStack> filter) {
        var state = new MatchingInventory(screen, filter);
        return new SlotQueryGenerator(state);
    }

    public static SlotQueryGenerator ofCustom(Iterable<ItemStack> items) {
        var state = new MatchingCustom(items);
        return new SlotQueryGenerator(state);
    }

    @Nullable
    public SlotQuery advance(BooleanSupplier shouldContinue) {
        if (this.results != null) {
            return this.results;
        }

        var state = this.state;
        while (shouldContinue.getAsBoolean()) {
            this.state = state = state.advance(shouldContinue);
            if (state instanceof Ready ready) {
                return this.results = ready.results();
            }
        }

        return null;
    }

    public void advanceFor(Duration duration) {
        if (this.results == null) {
            long targetTime = System.nanoTime() + duration.toNanos();
            this.advance(() -> System.nanoTime() < targetTime);
        }
    }

    public SlotQuery build() {
        var query = this.advance(() -> true);
        return Objects.requireNonNull(query);
    }

    private interface State {
        State advance(BooleanSupplier shouldContinue);
    }

    private static final class MatchingInventory implements State {
        private final Predicate<ItemStack> filter;
        private final Iterator<ItemStack> inventoryIterator;

        private final Set<ItemStack> inventoryMatches = ItemStackLinkedSet.createTypeAndComponentsSet();

        private MatchingInventory(AbstractContainerScreen<?> screen, Predicate<ItemStack> filter) {
            this.filter = filter;
            this.inventoryIterator = MenuItemStacks.of(screen).iterator();
        }

        @Override
        public State advance(BooleanSupplier shouldContinue) {
            var inventory = this.inventoryIterator;
            while (shouldContinue.getAsBoolean() && inventory.hasNext()) {
                var item = inventory.next();
                if (this.filter.test(item)) {
                    this.addInventoryMatch(item);
                }
            }

            if (!inventory.hasNext()) {
                return new MatchingGlobal(this.filter, this.inventoryMatches);
            } else {
                return this;
            }
        }

        private void addInventoryMatch(ItemStack item) {
            this.inventoryMatches.add(item.copyWithCount(1));
        }
    }

    private static final class MatchingGlobal implements State {
        private final Predicate<ItemStack> globalFilter;
        private final Set<ItemStack> inventoryMatches;

        private final Collection<ItemStack> globalItems = GlobalItemStacks.get(Minecraft.getInstance());
        private final Iterator<ItemStack> globalItemIterator = this.globalItems.iterator();

        private int matchCount;

        private MatchingGlobal(Predicate<ItemStack> filter, Set<ItemStack> inventoryMatches) {
            this.globalFilter = filter.and(item -> !inventoryMatches.contains(item));
            this.inventoryMatches = inventoryMatches;
        }

        @Override
        public State advance(BooleanSupplier shouldContinue) {
            var iterator = this.globalItemIterator;
            while (shouldContinue.getAsBoolean() && iterator.hasNext()) {
                var item = iterator.next();
                if (this.globalFilter.test(item)) {
                    this.matchCount++;
                }
            }

            if (!iterator.hasNext()) {
                var results = this.buildResults();
                return new Ready(results);
            } else {
                return this;
            }
        }

        private SlotQuery buildResults() {
            return new SlotQuery(ConcatList.of(
                    this.buildInventoryResults(),
                    this.buildGlobalResults()
            ));
        }

        private List<QueriedItem> buildGlobalResults() {
            return LazyFillingList.ofIterator(
                    Iterators.transform(
                            Iterators.filter(this.globalItems.iterator(), this.globalFilter::test),
                            QueriedItem::of
                    ),
                    this.matchCount
            );
        }

        private List<QueriedItem> buildInventoryResults() {
            var inventoryItems = new ObjectArrayList<QueriedItem>(this.inventoryMatches.size());
            for (var match : this.inventoryMatches) {
                inventoryItems.add(QueriedItem.ofHighlighted(match));
            }
            return inventoryItems;
        }
    }

    private static final class MatchingCustom implements State {
        private final Iterable<ItemStack> items;
        private final Iterator<ItemStack> itemIterator;

        private int itemCount;

        private MatchingCustom(Iterable<ItemStack> items) {
            this.items = items;
            this.itemIterator = items.iterator();
        }

        @Override
        public State advance(BooleanSupplier shouldContinue) {
            var iterator = this.itemIterator;
            while (shouldContinue.getAsBoolean() && iterator.hasNext()) {
                iterator.next();
                this.itemCount++;
            }

            if (!iterator.hasNext()) {
                var results = this.buildResults();
                return new Ready(results);
            } else {
                return this;
            }
        }

        private SlotQuery buildResults() {
            var items = Iterators.transform(this.items.iterator(), QueriedItem::of);
            return new SlotQuery(LazyFillingList.ofIterator(items, this.itemCount));
        }
    }

    private record Ready(SlotQuery results) implements State {
        @Override
        public State advance(BooleanSupplier shouldContinue) {
            return this;
        }
    }
}
