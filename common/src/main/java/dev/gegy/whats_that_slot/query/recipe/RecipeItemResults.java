package dev.gegy.whats_that_slot.query.recipe;

import com.google.common.collect.AbstractIterator;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Set;

public record RecipeItemResults(ClientRecipeBook recipes, ContextMap displayContext, SearchRecipeBookCategory category) implements Iterable<ItemStack> {
    @Override
    public Iterator<ItemStack> iterator() {
        var collectionIterator = this.recipes.getCollection(category).iterator();

        return new AbstractIterator<>() {
            private static final Hash.Strategy<ItemStack> MATCHING_ITEMS = new Hash.Strategy<>() {
                @Override
                public int hashCode(ItemStack item) {
                    return ItemStack.hashItemAndComponents(item);
                }

                @Override
                public boolean equals(ItemStack a, ItemStack b) {
                    return a == b || (a != null && b != null && ItemStack.isSameItemSameComponents(a, b));
                }
            };

            private Iterator<RecipeDisplayEntry> recipeIterator = Collections.emptyIterator();

            private final Set<ItemStack> encounteredItems = new ObjectOpenCustomHashSet<>(MATCHING_ITEMS);
            private final Deque<ItemStack> pendingResults = new ArrayDeque<>();

            @Nullable
            @Override
            protected ItemStack computeNext() {
                if (this.pendingResults.isEmpty()) {
                    if (!this.tryCollectResults()) {
                        return this.endOfData();
                    }
                }
                return this.pendingResults.remove().copyWithCount(1);
            }

            private boolean tryCollectResults() {
                while (this.recipeIterator.hasNext() || collectionIterator.hasNext()) {
                    if (!this.recipeIterator.hasNext()) {
                        this.recipeIterator = collectionIterator.next().getRecipes().iterator();
                        continue;
                    }
                    var recipe = this.recipeIterator.next();
                    for (var item : recipe.display().result().resolveForStacks(displayContext)) {
                        item = item.copyWithCount(1);
                        if (this.encounteredItems.add(item)) {
                            this.pendingResults.add(item);
                        }
                    }
                    if (!this.pendingResults.isEmpty()) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
