package net.enderturret.itemsubs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class SubmarineRelayBlock extends WaterloggedHorizontalBlock {

	public static final EnumProperty<SubmarinePresence> PRESENCE = EnumProperty.create("presence", SubmarinePresence.class);

	public SubmarineRelayBlock(Properties props) {
		super(props);
		registerDefaultState(defaultBlockState().setValue(PRESENCE, SubmarinePresence.NOT_PRESENT));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(PRESENCE);
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