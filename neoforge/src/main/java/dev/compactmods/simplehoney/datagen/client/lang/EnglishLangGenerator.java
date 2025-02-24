package dev.compactmods.simplehoney.datagen.client.lang;

import dev.compactmods.simplehoney.SimpleHoney;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnglishLangGenerator extends LanguageProvider {

    public EnglishLangGenerator(PackOutput output) {
        super(output, SimpleHoney.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(SimpleHoney.HONEY_DROP.get(), "Honey Drop");
    }
}
