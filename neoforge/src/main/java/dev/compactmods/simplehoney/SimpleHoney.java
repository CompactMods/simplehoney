package dev.compactmods.simplehoney;

import cpw.mods.modlauncher.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

@Mod(SimpleHoney.MOD_ID)
public class SimpleHoney {
    public static final String MOD_ID = "simplehoney";

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final FoodProperties HONEY_DROP_FOOD_PROPS = new FoodProperties.Builder()
            .alwaysEdible()
            .nutrition(2)
            .saturationModifier(0.1f)
            .build();

    public static final Item.Properties HONEY_DROP_ITEM_PROPS = new Item.Properties()
            .food(HONEY_DROP_FOOD_PROPS);

    public static final DeferredItem<Item> HONEY_DROP = ITEMS.register("honey_drop", () -> new Item(HONEY_DROP_ITEM_PROPS));

    public SimpleHoney(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::registerCaps);

        // NeoForge.EVENT_BUS.addListener(this::debugTickListener);
    }

    private void debugTickListener(LevelTickEvent.Pre event) {
        final var level = event.getLevel();

        if(level.tickRateManager().runsNormally() && level.getGameTime() % 10 == 0) {
            final var allPlayers = level.players();
            allPlayers.stream()
                    .filter(player -> player.isCreative() && player.getMainHandItem().is(HONEY_DROP))
                    .forEach(this::tickNearbyHives);
        }
    }


    private void tickNearbyHives(Player player) {
        final var level = player.level();
        final var nearbyStates = BlockPos.betweenClosedStream(AABB.ofSize(player.position(), 10, 10, 10));
        nearbyStates
                .map(pos -> new Tuple<>(pos, level.getBlockState(pos)))
                .filter(posAndState -> posAndState.getB().is(Blocks.BEEHIVE) || posAndState.getB().is(Blocks.BEE_NEST))
                .filter(posAndHive -> posAndHive.getB().getValue(BeehiveBlock.HONEY_LEVEL) < BeehiveBlock.MAX_HONEY_LEVELS)
                .forEach(posAndHive -> {
                    var newState = posAndHive.getB().setValue(BeehiveBlock.HONEY_LEVEL, posAndHive.getB().getValue(BeehiveBlock.HONEY_LEVEL) + 1);
                    level.setBlock(posAndHive.getA(), newState, BeehiveBlock.UPDATE_ALL);
                });
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
