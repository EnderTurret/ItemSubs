package net.enderturret.itemsubs.block.entity;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import net.enderturret.itemsubs.entity.SubmarineEntity;
import net.enderturret.itemsubs.init.ISBlockEntityTypes;

public class SubmarineStationBlockEntity extends BlockEntity {

	private SubmarineEntity docked;
	private boolean queryDocked = false;

	public SubmarineStationBlockEntity(BlockEntityType<? extends SubmarineStationBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public SubmarineStationBlockEntity(BlockPos pos, BlockState state) {
		this(ISBlockEntityTypes.SUBMARINE_STATION.get(), pos, state);
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (docked() != null)
			return docked.getCapability(cap, side);

		return super.getCapability(cap, side);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		docked = null;
		queryDocked = true;
	}

	public void setDocked(@Nullable SubmarineEntity entity) {
		docked = entity;
		queryDocked = false;
	}

	@Nullable
	public SubmarineEntity docked() {
		if (queryDocked && level instanceof ServerLevel serverLevel) {
			final List<SubmarineEntity> subs = serverLevel.getEntitiesOfClass(SubmarineEntity.class, new AABB(getBlockPos()));
			docked = subs.isEmpty() ? null : subs.get(0);
			queryDocked = false;
		}

		return docked;
	}
}