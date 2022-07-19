package net.enderturret.itemsubs.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SubmarineStationBlock extends WaterloggedHorizontalBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public SubmarineStationBlock(Properties props) {
		super(props);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}
}