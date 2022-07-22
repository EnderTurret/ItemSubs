package net.enderturret.itemsubs.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.enderturret.itemsubs.entity.SubmarineEntity;

public interface ISubmarineBlock {

	/**
	 * Called by submarines when they're over or inside this block to get a direction to turn towards.
	 * This should return the direction of the block, if this block should re-orient the submarine.
	 * A {@code null} value indicates that the submarine should not turn.
	 * @param state The {@link BlockState} at {@code pos}.
	 * @param level The world the block is in.
	 * @param pos The location of the block in the world.
	 * @param entity The submarine that is turning. May be {@code null}.
	 * @param over Whether the submarine is over this block or inside it.
	 * @return The new direction.
	 */
	@Nullable
	public Direction getOrientation(BlockState state, Level level, BlockPos pos, @Nullable SubmarineEntity entity, boolean over);

	/**
	 * Called when a submarine is moving over or inside this block.
	 * This can be used to update submarine references or update block data.
	 * @param state The {@link BlockState} at {@code pos}.
	 * @param level The world the block is in.
	 * @param pos The location of the block in the world.
	 * @param entity The submarine that is moving.
	 * @param over Whether the submarine is over this block or inside it.
	 */
	public default void onSubmarineOver(BlockState state, Level level, BlockPos pos, SubmarineEntity entity, boolean over) {}

	/**
	 * Called when a submarine is "docked" over or inside this block.
	 * The submarine will by default keep moving in its current direction.
	 * If you want to stop the submarine and keep it in place, call {@link SubmarineEntity#setMoving(boolean)}.
	 * <p>
	 * This method can also be used to update data on the block.
	 * For example, {@linkplain SubmarineRelayBlock the relay block} uses this method to calculate its comparator output.
	 * </p>
	 * @param state The {@link BlockState} at {@code pos}.
	 * @param level The world the block is in.
	 * @param pos The location of the block in the world.
	 * @param entity The submarine that is docked.
	 * @param over Whether the submarine is over this block or inside it.
	 */
	public default void onSubmarineDocked(BlockState state, Level level, BlockPos pos, SubmarineEntity entity, boolean over) {}

	/**
	 * Called when a submarine is leaving this block.
	 * This can be used similarly to {@link #onSubmarineOver(BlockState, Level, BlockPos, SubmarineEntity, boolean)}.
	 * @param state The {@link BlockState} at {@code pos}.
	 * @param level The world the block is in.
	 * @param pos The location of the block in the world.
	 * @param entity The submarine that is leaving.
	 * @param over Whether the submarine is over this block or inside it.
	 */
	public default void onSubmarineLeaving(BlockState state, Level level, BlockPos pos, SubmarineEntity entity, boolean over) {}
}