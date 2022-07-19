package net.enderturret.itemsubs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class SubmarineRelayBlock extends HorizontalDirectionalBlock {

	public static final EnumProperty<SubmarinePresence> PRESENCE = EnumProperty.create("presence", SubmarinePresence.class);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public SubmarineRelayBlock(Properties props) {
		super(props);
		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(PRESENCE, SubmarinePresence.NOT_PRESENT)
				.setValue(POWERED, false));
	}

	public Direction getOrientation(BlockState state, Level level, BlockPos pos) {
		// Remember when we called this EnumFacing?
		final Direction facing = state.getValue(FACING);
		return state.getValue(POWERED) ? facing.getOpposite() : facing;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, PRESENCE, POWERED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection());
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos)
				.setValue(PRESENCE, SubmarinePresence.NOT_PRESENT);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return state.getValue(PRESENCE) != SubmarinePresence.NOT_PRESENT;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return switch (state.getValue(PRESENCE)) {
		case NOT_PRESENT -> 0;
		case PRESENT -> 1;
		case TURNING -> 2;
		};
	}
}