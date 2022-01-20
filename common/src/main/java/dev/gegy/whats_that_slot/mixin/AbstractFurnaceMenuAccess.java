package dev.gegy.whats_that_slot.mixin;

import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceMenu.class)
public interface AbstractFurnaceMenuAccess {
    @Accessor("recipeType")
    RecipeType<? extends AbstractCookingRecipe> getRecipeType();
}
