package dev.compactmods.simplehoney;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HiveItemHandler implements ResourceHandler<ItemResource> {

    private final Level level;
    private final BlockPos blockPos;

    public HiveItemHandler(Level level, BlockPos blockPos) {
        this.level = level;
        this.blockPos = blockPos;
    }

    public boolean readyToExtract() {
        final var state = level.getBlockState(blockPos);
        if(state.is(Blocks.BEEHIVE) || state.is(Blocks.BEE_NEST)) {
            return state.getValue(BeehiveBlock.HONEY_LEVEL) >= 5;
        } else {
            return false;
        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public ItemResource getResource(int slot) {
        TransferPreconditions.checkNonNegative(slot);
        if(slot != 0) return ItemResource.EMPTY;
        return ItemResource.of(SimpleHoney.HONEY_DROP.get());
    }

    @Override
    public long getAmountAsLong(int i) {
        return i == 0 && readyToExtract() ? 1 : 0;
    }

    @Override
    public long getCapacityAsLong(int i, ItemResource resource) {
        return 0;
    }

    @Override
    public boolean isValid(int i, ItemResource resource) {
        return false;
    }

    @Override
    public int insert(int i, ItemResource resource, int i1, TransactionContext ctx) {
        return 0;
    }

    @Override
    public int extract(int slot, ItemResource resource, int i1, TransactionContext ctx) {
        if(slot != 0)
            return 0;

        if(!readyToExtract())
            return 0;

        level.setBlockAndUpdate(blockPos, level.getBlockState(blockPos).setValue(BeehiveBlock.HONEY_LEVEL, 0));
        return 1;
    }
}
