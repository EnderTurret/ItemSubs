package net.enderturret.itemsubs.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

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
	 * Called by submarines to determine the collision shape of this block.
	 * This can be used to customize the shape that is checked.
	 * Be careful, as this could cause submarines to clip through the block.
	 * @param state The {@link BlockState} at {@code pos}.
	 * @param level The world the block is in.
	 * @param pos The location of the block in the world.
	 * @param entity The submarine that is acquiring the shape. May be {@code null}.
	 * @return The collision shape.
	 */
	public default VoxelShape getSubmarineCollisionShape(BlockState state, Level level, BlockPos pos, @Nullable SubmarineEntity entity) {
		return state.getCollisionShape(level, pos);
	}

	/**
	 * Called by submarines to determine if they may enter this block.
	 * This can be used to allow or deny entry even if the collision shape disagrees.
	 * Returning {@code null} will cause the submarine to check the {@linkplain #getSubmarineCollisionShape(BlockState, Level, BlockPos, SubmarineEntity) collision shape} instead.
	 * @param state The {@link BlockState} at {@code pos}.
	 * @param level The world the block is in.
	 * @param pos The location of the block in the world.
	 * @param enterDirection The direction the submarine is entering from. If {@code null}, implies the submarine is being placed.
	 * @param entity The submarine that is attempting to enter. {@code null} during placement.
	 * @return Whether the submarine may enter from the given side, or {@code null} to check the collision shape.
	 */
	@Nullable
	public default Boolean canSubmarineEnter(BlockState state, Level level, BlockPos pos, @Nullable Direction enterDirection, @Nullable SubmarineEntity entity) {
		return null;
	}

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

	/**
	 * Called when a submarine's inventory changes while inside this block.
	 * @param state The {@link BlockState} at {@code pos}.
	 * @param level The world the block is in.
	 * @param pos The location of the block in the world.
	 * @param entity The submarine whose inventory has changed.
	 */
	public default void onSubmarineInventoryChanged(BlockState state, Level level, BlockPos pos, SubmarineEntity entity) {}
}