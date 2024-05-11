package dev.compactmods.simplehoney.datagen;

import dev.compactmods.simplehoney.SimpleHoney;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> holder) {
        super(packOutput, holder);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, new ItemStack(Items.HONEY_BOTTLE))
                .requires(SimpleHoney.HONEY_DROP)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("honey_drop", has(SimpleHoney.HONEY_DROP))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, new ItemStack(Items.HONEYCOMB))
                .requires(SimpleHoney.HONEY_DROP)
                .unlockedBy("honey_drop", has(SimpleHoney.HONEY_DROP))
                .save(recipeOutput);
    }
}
