package net.enderturret.itemsubs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SubmarineStationBlock extends WaterloggedHorizontalBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	private static final VoxelShape[] STATES = CollisionSupport.horizontal(
			CollisionSupport.of(
					box(0, 0, 0, 16, 3, 16),
					box(0, 3, 0, 1, 16, 16),
					box(15, 3, 0, 16, 16, 16),
					box(0, 3, 15, 16, 16, 16)), Direction.NORTH);

	public SubmarineStationBlock(Properties props) {
		super(props);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context)
				.setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (state.getValue(POWERED) != level.hasNeighborSignal(pos))
			level.setBlock(pos, state.cycle(POWERED), UPDATE_CLIENTS);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return STATES[state.getValue(FACING).get2DDataValue()];
	}
}