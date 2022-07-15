package net.enderturret.itemsubs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class WaterloggedHorizontalBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public WaterloggedHorizontalBlock(Properties props) {
		super(props);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());

		return defaultBlockState()
				.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER)
				.setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED))
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}
}