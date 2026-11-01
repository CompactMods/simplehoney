package dev.compactmods.simplehoney;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;

public class HiveSnapshotJournal extends SnapshotJournal<BlockState> {

    private final Level level;
    private final BlockPos position;

    public HiveSnapshotJournal(final Level level, final BlockPos pos) {
        this.level = level;
        this.position = pos;
    }

    @Override
    protected BlockState createSnapshot() {
        return level.getBlockState(position);
    }

    @Override
    protected void revertToSnapshot(BlockState snapshot) {
        level.setBlock(position, snapshot, Block.UPDATE_ALL);
    }
}
