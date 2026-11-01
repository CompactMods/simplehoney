package dev.compactmods.simplehoney.datagen;

import dev.compactmods.simplehoney.SimpleHoney;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class ItemModelGenerator extends ModelProvider {

    public ItemModelGenerator(PackOutput packOutput) {
        super(packOutput, SimpleHoney.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(SimpleHoney.HONEY_DROP.get(), ModelTemplates.FLAT_ITEM);
    }
}
