package net.enderturret.itemsubs.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import net.enderturret.itemsubs.entity.SubmarineEntity;
import net.enderturret.itemsubs.init.ISEntityTypes;

public class SubmarineItem extends Item {

	public SubmarineItem(Item.Properties props) {
		super(props);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		final Level level = ctx.getLevel();
		final BlockPos pos = ctx.getClickedPos();
		final Direction clickedFace = ctx.getClickedFace();

		final Vec3 spawnPos = new Vec3(
				ctx.getClickLocation().x + clickedFace.getStepX() * .5,
				ctx.getClickLocation().y + (clickedFace == Direction.DOWN ? -0.75 : 0.25),
				ctx.getClickLocation().z + clickedFace.getStepZ() * .5
				);

		final BlockState state = level.getBlockState(new BlockPos(spawnPos));

		if (!state.getFluidState().is(FluidTags.WATER))
			return InteractionResult.FAIL;

		final ItemStack stack = ctx.getItemInHand();

		if (!level.isClientSide) {
			final SubmarineEntity sub = ISEntityTypes.SUBMARINE.get().create(level);
			sub.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

			sub.readItemData(stack);

			level.addFreshEntity(sub);
			level.gameEvent(GameEvent.ENTITY_PLACE, pos, GameEvent.Context.of(ctx.getPlayer(), level.getBlockState(pos.below())));
		}

		stack.shrink(1);

		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}