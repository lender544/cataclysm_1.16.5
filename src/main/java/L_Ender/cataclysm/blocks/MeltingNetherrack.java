package L_Ender.cataclysm.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class MeltingNetherrack extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;

    public MeltingNetherrack(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
    }
    public void tick(BlockState blockState, ServerWorld serverLevel, BlockPos blockPos, Random randomSource) {
        if (((randomSource.nextInt(3) == 0 && this.slightlyMelt(blockState, serverLevel, blockPos)))) {
            serverLevel.getPendingBlockTicks().scheduleTick(blockPos, this, MathHelper.nextInt(randomSource, 20, 40));
        }
    }


    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    private boolean slightlyMelt(BlockState blockState, ServerWorld level, BlockPos blockPos) {
        final int MAX_AGE_BEFORE_LAVA = 3;
        int i = blockState.get(AGE);
        if (i < MAX_AGE_BEFORE_LAVA) {
            level.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(i + 1)), 2);
            return false;
        } else {
            this.melt(blockState, level, blockPos);
            return true;
        }
    }

    private void melt(BlockState blockState, ServerWorld level, BlockPos blockPos) {
        level.setBlockState(blockPos, Blocks.LAVA.getDefaultState());
    }
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }
}
