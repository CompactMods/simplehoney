package dev.compactmods.simplehoney;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

@Mod(SimpleHoney.MOD_ID)
public class SimpleHoney {


    public static final String MOD_ID = "simplehoney";

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final FoodProperties HONEY_DROP_FOOD_PROPS = new FoodProperties.Builder()
            .alwaysEdible()
            .nutrition(2)
            .saturationModifier(0.1f)
            .build();

    public static final DeferredItem<Item> HONEY_DROP = ITEMS.registerSimpleItem("honey_drop", p -> p.food(HONEY_DROP_FOOD_PROPS));

    public SimpleHoney(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::registerCaps);
        modEventBus.addListener(this::onCreativeTabs);

//         NeoForge.EVENT_BUS.addListener(this::debugTickListener);
    }

    protected void onCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey().equals(CreativeModeTabs.FOOD_AND_DRINKS)) {
            event.accept(HONEY_DROP.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

//    private record BlockPosAndState(BlockPos pos, BlockState state){}
//
//    private void debugTickListener(LevelTickEvent.Pre event) {
//        final var level = event.getLevel();
//
//        if(level.tickRateManager().runsNormally() && level.getGameTime() % 10 == 0) {
//            final var allPlayers = level.players();
//            allPlayers.stream()
//                    .filter(player -> player.isCreative() && player.getMainHandItem().is(HONEY_DROP))
//                    .forEach(this::tickNearbyHives);
//        }
//    }
//
//    private void tickNearbyHives(Player player) {
//        final var level = player.level();
//        final var nearbyStates = BlockPos.betweenClosedStream(AABB.ofSize(player.position(), 10, 10, 10));
//        nearbyStates
//                .map(pos -> new BlockPosAndState(pos, level.getBlockState(pos)))
//                .filter(posAndState -> posAndState.state().is(Blocks.BEEHIVE) || posAndState.state().is(Blocks.BEE_NEST))
//                .filter(posAndHive -> posAndHive.state().getValue(BeehiveBlock.HONEY_LEVEL) < BeehiveBlock.MAX_HONEY_LEVELS)
//                .forEach(posAndHive -> {
//                    var newState = posAndHive.state().setValue(BeehiveBlock.HONEY_LEVEL, posAndHive.state().getValue(BeehiveBlock.HONEY_LEVEL) + 1);
//                    level.setBlock(posAndHive.pos(), newState, BeehiveBlock.UPDATE_ALL);
//                });
//    }

    private void registerCaps(final RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.Item.BLOCK, this::honeyCap, Blocks.BEEHIVE, Blocks.BEE_NEST);
        // event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack.Consumable(stack, 250), HONEY_DROP);
    }

    private ResourceHandler<ItemResource> honeyCap(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity, Direction direction) {
        if(direction == null || direction.getAxis().isHorizontal())
            return null;

        if(blockEntity instanceof BeehiveBlockEntity)
            return new HiveItemHandler(level, blockPos);

        return null;
    }
}
