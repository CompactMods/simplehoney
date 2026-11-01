package dev.compactmods.simplehoney.datagen;

import dev.compactmods.simplehoney.SimpleHoney;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeGenerator extends RecipeProvider {

    public RecipeGenerator(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    @Override
    protected void buildRecipes() {
        shapeless(RecipeCategory.FOOD, new ItemStackTemplate(Items.HONEY_BOTTLE))
                .requires(SimpleHoney.HONEY_DROP)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("honey_drop", has(SimpleHoney.HONEY_DROP))
                .save(output);

        shapeless(RecipeCategory.FOOD, new ItemStackTemplate(Items.HONEYCOMB))
                .requires(SimpleHoney.HONEY_DROP)
                .unlockedBy("honey_drop", has(SimpleHoney.HONEY_DROP))
                .save(output);
    }
}
