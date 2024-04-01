package dev.compactmods.simplehoney;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.items.IItemHandler;

public class HiveItemHandler implements IItemHandler {

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
    public int getSlotLimit(int slot) {
        return slot == 0 ? 1 : 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if(slot != 0) return ItemStack.EMPTY;
        return readyToExtract() ? new ItemStack(SimpleHoney.HONEY_DROP.get()) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(slot != 0 || amount == 0) return ItemStack.EMPTY;

        if(!readyToExtract()) return ItemStack.EMPTY;

        final var drop = new ItemStack(SimpleHoney.HONEY_DROP.get());
        if(simulate) {
            return drop;
        }

        level.setBlockAndUpdate(blockPos, level.getBlockState(blockPos).setValue(BeehiveBlock.HONEY_LEVEL, 0));
        return drop;
    }
}
