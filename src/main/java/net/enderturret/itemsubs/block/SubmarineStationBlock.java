package net.enderturret.itemsubs.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.enderturret.itemsubs.block.entity.SubmarineStationBlockEntity;
import net.enderturret.itemsubs.entity.SubmarineEntity;

public class SubmarineStationBlock extends WaterloggedHorizontalBlock implements ISubmarineBlock, EntityBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	private static final VoxelShape[] OCCLUSION = CollisionSupport.horizontal(
			CollisionSupport.of(
					box(0, 0, 0, 16, 3, 16) // base
					), Direction.NORTH);

	private static final VoxelShape[] STATES = CollisionSupport.horizontal(
			CollisionSupport.of(
					box(0, 0, 0, 16, 3, 16), // base
					box(0, 3, 0, 1, 16, 16), // right wall
					box(15, 3, 0, 16, 16, 16), // left wall
					box(1, 15, 7, 6, 16, 9), // right top bar
					box(10, 15, 7, 15, 16, 9), // left top bar
					box(6, 15, 6, 10, 16, 10), // center top component
					box(1, 15, 0, 16, 16, 1),
					box(1, 15, 15, 16, 16, 16)
					), Direction.NORTH);

	public SubmarineStationBlock(Properties props) {
		super(props);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return OCCLUSION[state.getValue(FACING).get2DDataValue()];
	}

	@Override
	@Nullable
	public Direction getOrientation(BlockState state, Level level, BlockPos pos, @Nullable SubmarineEntity entity, boolean over) {
		return null;
	}

	@Override
	@Nullable
	public Boolean canSubmarineEnter(BlockState state, Level level, BlockPos pos, @Nullable Direction enterDirection, @Nullable SubmarineEntity entity) {
		if (enterDirection == null) return true;

		// Allow the submarine to enter from the front or the back.
		return state.getValue(FACING).getAxis() == enterDirection.getAxis() ? true : null;
	}

	@Override
	public void onSubmarineDocked(BlockState state, Level level, BlockPos pos, SubmarineEntity entity, boolean over) {
		if (!over) {
			entity.setMoving(state.getValue(POWERED));
			final SubmarineStationBlockEntity ssbe = (SubmarineStationBlockEntity) level.getBlockEntity(pos);
			ssbe.setDocked(entity);
		}
	}

	@Override
	public void onSubmarineLeaving(BlockState state, Level level, BlockPos pos, SubmarineEntity entity, boolean over) {
		final SubmarineStationBlockEntity ssbe = (SubmarineStationBlockEntity) level.getBlockEntity(pos);
		ssbe.setDocked(null);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SubmarineStationBlockEntity(pos, state);
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
		if (state.getValue(POWERED) != level.hasNeighborSignal(pos)) {
			final BlockState newState = state.cycle(POWERED);
			level.setBlock(pos, newState, UPDATE_CLIENTS);
			if (newState.getValue(POWERED)) {
				final SubmarineStationBlockEntity ssbe = (SubmarineStationBlockEntity) level.getBlockEntity(pos);
				if (ssbe.docked() != null)
					ssbe.docked().setMoving(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return STATES[state.getValue(FACING).get2DDataValue()];
	}

	@Override
	public void onSubmarineInventoryChanged(BlockState state, Level level, BlockPos pos, SubmarineEntity entity) {
		((SubmarineStationBlockEntity) level.getBlockEntity(pos)).onSubmarineInventoryChanged(state, level, pos, entity);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return ((SubmarineStationBlockEntity) level.getBlockEntity(pos)).getAnalogOutputSignal(state, level, pos);
	}
}