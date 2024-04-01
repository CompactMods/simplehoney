package dev.compactmods.simplehoney.datagen;

import dev.compactmods.simplehoney.SimpleHoney;
import dev.compactmods.simplehoney.datagen.lang.EnglishLangGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = SimpleHoney.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGeneration {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        final var fileHelper = event.getExistingFileHelper();
        final var generator = event.getGenerator();

        final var packOut = generator.getPackOutput();

        // Server
        boolean server = event.includeServer();
        generator.addProvider(server, new RecipeGenerator(packOut));

        // Client
        boolean client = event.includeClient();
        generator.addProvider(client, new ItemModelGenerator(packOut, fileHelper));
        generator.addProvider(client, new EnglishLangGenerator(generator));
    }
}
