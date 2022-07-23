package net.enderturret.itemsubs.block.entity;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
		lastState = -1;
	}

	/*
	 * 0 = submarine not present
	 * 1 = submarine docked
	 * 2 = submarine docked, inventory empty
	 * 3 = submarine docked, inventory full
	 */
	private int lastState = -1;

	public void onSubmarineInventoryChanged(BlockState state, Level level, BlockPos pos, SubmarineEntity entity) {
		updateAnalogState();
	}

	protected int getAnalogState(@Nullable SubmarineEntity entity) {
		if (entity == null) return 0;

		boolean allEmpty = true;
		boolean allFull = true;

		// Slot = 2, so we skip the fuel and upgrade slots.
		for (int slot = 2; slot < entity.getContainer().getContainerSize(); slot++) {
			final ItemStack stack = entity.getContainer().getItem(slot);

			if (stack.isEmpty())
				allFull = false;
			else
				allEmpty = false;

			if (!allEmpty && !allFull)
				break;
		}

		// This catches the case of there being no items (true == true),
		// as well as the case where there is a mix of empty/non-empty items (false == false).
		if (allEmpty == allFull)
			return allEmpty ? 2 : 1;
		if (allEmpty)
			return 2;

		return 3;
	}

	protected void updateAnalogState() {
		final int newState = getAnalogState(docked);
		if (lastState == -1 || lastState != newState) {
			lastState = newState;
			level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
		}
	}

	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		//System.out.println("[" + EffectiveSide.get() + "] " + lastState + ", " + docked);
		return docked == null ? (lastState = 0) : lastState;
	}

	public void setDocked(@Nullable SubmarineEntity entity) {
		docked = entity;
		queryDocked = false;
		updateAnalogState();
	}

	@Nullable
	public SubmarineEntity docked() {
		if (queryDocked && level instanceof ServerLevel serverLevel) {
			final List<SubmarineEntity> subs = serverLevel.getEntitiesOfClass(SubmarineEntity.class, new AABB(getBlockPos()));
			docked = subs.isEmpty() ? null : subs.get(0);
			queryDocked = false;
			updateAnalogState();
		}

		return docked;
	}
}