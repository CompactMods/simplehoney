package dev.compactmods.simplehoney;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;

public class HiveItemHandler implements ResourceHandler<ItemResource> {

    private final Level level;
    private final BlockPos blockPos;
    private final HiveSnapshotJournal journal;

    private final ItemResource HONEY_DROP;

    public HiveItemHandler(Level level, BlockPos blockPos) {
        this.level = level;
        this.blockPos = blockPos;
        this.journal = new HiveSnapshotJournal(level, blockPos);
        this.HONEY_DROP = ItemResource.of(SimpleHoney.HONEY_DROP.get());
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
    public ItemResource getResource(int index) {
        if(index != 0 || !readyToExtract())
            return ItemResource.EMPTY;

        return HONEY_DROP;
    }

    @Override
    public long getAmountAsLong(int index) {
        return readyToExtract() ? 1 : 0;
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return 0;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return false;
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, @NonNull TransactionContext transaction) {
        return 0;
    }

    @Override
    public int extract(int slot, ItemResource resource, int amount, @NonNull TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, slot);

        if(!readyToExtract() || !resource.equals(HONEY_DROP))
            return 0;

        journal.updateSnapshots(transaction);
        level.setBlockAndUpdate(blockPos, level.getBlockState(blockPos).setValue(BeehiveBlock.HONEY_LEVEL, 0));
        return 1;
    }
}
