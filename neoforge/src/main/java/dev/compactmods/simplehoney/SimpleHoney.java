package dev.compactmods.simplehoney;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(SimpleHoney.MOD_ID)
public class SimpleHoney {
    public static final String MOD_ID = "simplehoney";

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final FoodProperties HONEY_DROP_FOOD_PROPS = new FoodProperties.Builder()
            .alwaysEat()
            .nutrition(2)
            .saturationMod(0.1f)
            .build();

    public static final Item.Properties HONEY_DROP_ITEM_PROPS = new Item.Properties()
            .food(HONEY_DROP_FOOD_PROPS);

    public static final DeferredItem<Item> HONEY_DROP = ITEMS.register("honey_drop", () -> new Item(HONEY_DROP_ITEM_PROPS));

    public SimpleHoney(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::registerCaps);
    }

    private void registerCaps(final RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.ItemHandler.BLOCK, this::honeyCap, Blocks.BEEHIVE, Blocks.BEE_NEST);
        // event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack.Consumable(stack, 250), HONEY_DROP);
    }

    private IItemHandler honeyCap(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity, Direction direction) {
        if(direction == null || direction.getAxis().isHorizontal())
            return null;

        if(blockEntity instanceof BeehiveBlockEntity)
            return new HiveItemHandler(level, blockPos);

        return null;
    }
}
