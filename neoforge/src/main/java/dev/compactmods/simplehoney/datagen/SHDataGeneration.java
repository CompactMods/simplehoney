package dev.compactmods.simplehoney.datagen;

import dev.compactmods.simplehoney.SimpleHoney;
import dev.compactmods.simplehoney.datagen.client.ItemAndBlockModels;
import dev.compactmods.simplehoney.datagen.client.lang.EnglishLangGenerator;
import dev.compactmods.simplehoney.datagen.server.SHRecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = SimpleHoney.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SHDataGeneration {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        // Client
        event.createProvider(ItemAndBlockModels::new);
        event.createProvider(EnglishLangGenerator::new);

        // Server
        event.createProvider(SHRecipeProvider.Runner::new);
    }
}
