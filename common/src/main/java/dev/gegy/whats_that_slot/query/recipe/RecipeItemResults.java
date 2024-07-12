package dev.gegy.whats_that_slot.query.recipe;

import com.google.common.collect.AbstractIterator;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public record RecipeItemResults(RegistryAccess registryAccess, RecipeManager recipes, RecipeType<?> recipeType) implements Iterable<ItemStack> {
    @Override
    public Iterator<ItemStack> iterator() {
        var recipeIterator = this.recipes.getRecipes().iterator();

        return new AbstractIterator<>() {
            private static final Hash.Strategy<ItemStack> MATCHING_ITEMS = new Hash.Strategy<>() {
                @Override
                public int hashCode(ItemStack item) {
                    if (item != null) {
                        return Objects.hash(item.getItem(), item.getComponents());
                    } else {
                        return 0;
                    }
                }

                @Override
                public boolean equals(ItemStack a, ItemStack b) {
                    return a == b || (a != null && b != null && ItemStack.isSameItemSameComponents(a, b));
                }
            };

            private final Set<ItemStack> encounteredItems = new ObjectOpenCustomHashSet<>(MATCHING_ITEMS);

            @Nullable
            @Override
            protected ItemStack computeNext() {
                while (recipeIterator.hasNext()) {
                    var recipe = recipeIterator.next().value();
                    if (recipe.getType() != RecipeItemResults.this.recipeType) {
                        continue;
                    }

                    var item = recipe.getResultItem(registryAccess).copyWithCount(1);
                    if (this.encounteredItems.add(item)) {
                        return item;
                    }
                }

                return this.endOfData();
            }
        };
    }
}
