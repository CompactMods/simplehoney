package dev.compactmods.simplehoney.datagen;

import dev.compactmods.simplehoney.SimpleHoney;
import dev.compactmods.simplehoney.datagen.lang.EnglishLangGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = SimpleHoney.MOD_ID)
public class DataGeneration {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {

        // Loot tables and recipes are reloadable registries now
        event.createReloadableRegistryObjects(new RegistrySetBuilder()
                        .add(Registries.LOOT_TABLE, new LootTableProvider(
                                Collections.emptySet(),
                                Collections.emptyList()))
                        .add(RecipeProvider.asBootstrap(RecipeGenerator::new)),
                Set.of(SimpleHoney.MOD_ID));

        // Client
        event.createProvider(ItemModelGenerator::new);
        event.createProvider(EnglishLangGenerator::new);
    }
}
