package net.enderturret.itemsubs.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import net.enderturret.itemsubs.ISConfig;
import net.enderturret.itemsubs.block.ISubmarineBlock;
import net.enderturret.itemsubs.block.SubmarineStationBlock;
import net.enderturret.itemsubs.entity.SubmarineEntity;
import net.enderturret.itemsubs.init.ISEntityTypes;

public class SubmarineItem extends Item {

	public SubmarineItem(Item.Properties props) {
		super(props);
	}

	protected boolean canPlace(Level level, BlockPos pos, BlockState state, UseOnContext ctx) {
		return checkFluid(level, pos, state, ctx) && SubmarineEntity.checkDefaultBlockCollision(level, pos, null, state);
	}

	protected boolean checkFluid(Level level, BlockPos pos, BlockState state, UseOnContext ctx) {
		return !ISConfig.get().realismMode() || state.getFluidState().is(FluidTags.WATER);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		final Level level = ctx.getLevel();
		final BlockPos pos = ctx.getClickedPos();
		final Direction clickedFace = ctx.getClickedFace();

		final Vec3 spawnPos;
		final Direction facing;

		final BlockState startState = level.getBlockState(pos);
		if (startState.getBlock() instanceof SubmarineStationBlock) {
			spawnPos = new Vec3(
					pos.getX() + .5,
					pos.getY() + .25,
					pos.getZ() + .5);
			facing = startState.getValue(SubmarineStationBlock.FACING);
		}

		else {
			final double yOffset;
			if (clickedFace == Direction.DOWN)
				yOffset = -0.75;
			else if (clickedFace == Direction.UP)
				yOffset = 1.25;
			else
				yOffset = .25;

			spawnPos = new Vec3(
					pos.getX() + clickedFace.getStepX() * 1 + 0.5,
					pos.getY() + yOffset,
					pos.getZ() + clickedFace.getStepZ() * 1 + 0.5
					);
			facing = ctx.getHorizontalDirection();
		}

		final BlockPos realPos = new BlockPos(spawnPos);
		final BlockState state = level.getBlockState(realPos);

		if (!canPlace(level, realPos, state, ctx))
			return InteractionResult.FAIL;

		final ItemStack stack = ctx.getItemInHand();

		if (!level.isClientSide) {
			final SubmarineEntity sub = ISEntityTypes.SUBMARINE.get().create(level);
			sub.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
			sub.setYRot(facing.toYRot());

			sub.readItemData(stack);

			level.addFreshEntity(sub);
			level.gameEvent(GameEvent.ENTITY_PLACE, pos, GameEvent.Context.of(ctx.getPlayer(), level.getBlockState(pos.below())));

			if (state.getBlock() instanceof ISubmarineBlock block)
				block.onSubmarineDocked(state, level, pos, sub, false);
		}

		stack.shrink(1);

		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}