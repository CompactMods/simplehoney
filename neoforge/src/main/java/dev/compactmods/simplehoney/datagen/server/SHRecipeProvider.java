package dev.compactmods.simplehoney.datagen.server;

import dev.compactmods.simplehoney.SimpleHoney;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class SHRecipeProvider extends RecipeProvider {

    protected SHRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shapeless(RecipeCategory.FOOD, new ItemStack(Items.HONEY_BOTTLE))
                .requires(SimpleHoney.HONEY_DROP)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("honey_drop", has(SimpleHoney.HONEY_DROP))
                .save(this.output);

        shapeless(RecipeCategory.FOOD, new ItemStack(Items.HONEYCOMB))
                .requires(SimpleHoney.HONEY_DROP)
                .unlockedBy("honey_drop", has(SimpleHoney.HONEY_DROP))
                .save(this.output);
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
            return new SHRecipeProvider(lookupProvider, output);
        }

        public String getName() {
            return "NeoForge recipes";
        }
    }
}
