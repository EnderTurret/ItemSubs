package net.enderturret.itemsubs.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.enderturret.itemsubs.init.ISBlockEntityTypes;

public class SubmarineStationBlockEntity extends BlockEntity {

	public SubmarineStationBlockEntity(BlockEntityType<? extends SubmarineStationBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public SubmarineStationBlockEntity(BlockPos pos, BlockState state) {
		this(ISBlockEntityTypes.SUBMARINE_STATION.get(), pos, state);
	}
}