package dev.compactmods.simplehoney.datagen;

import dev.compactmods.simplehoney.SimpleHoney;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, SimpleHoney.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basic(SimpleHoney.HONEY_DROP).texture("layer0", modLoc("item/honey_drop"));
    }

    private ItemModelBuilder basic(ResourceLocation name) {
        return withExistingParent(name.getPath(), mcLoc("item/generated"));
    }

    private ItemModelBuilder basic(Supplier<Item> supplier) {
        Item i = supplier.get();
        return basic(BuiltInRegistries.ITEM.getKey(i));
    }
}
