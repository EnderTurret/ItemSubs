package net.enderturret.itemsubs.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import net.enderturret.itemsubs.block.SubmarinePresence;
import net.enderturret.itemsubs.block.SubmarineRelayBlock;
import net.enderturret.itemsubs.init.ISEntityTypes;
import net.enderturret.itemsubs.init.ISItems;
import net.enderturret.itemsubs.menu.SubmarineMenu;

public class SubmarineEntity extends Entity {

	private static final EntityDataAccessor<Integer> HURT = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> HURTDIR = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.BOOLEAN);

	private static final Component NAME = Component.translatable("entity.itemsubs.submarine");

	private final SimpleContainer container = new SimpleContainer(9 * 2 + 2) {
		@Override
		public boolean stillValid(Player player) {
			return !isRemoved() && position().closerThan(player.position(), 8);
		}
	};

	private LazyOptional<?> containerCap = LazyOptional.of(() -> new InvWrapper(container));

	public SubmarineEntity(EntityType<? extends SubmarineEntity> type, Level level) {
		super(type, level);
	}

	public SubmarineEntity(Level level) {
		this(ISEntityTypes.SUBMARINE.get(), level);
	}

	public SimpleContainer getContainer() {
		return container;
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		final InteractionResult result = super.interact(player, hand);
		if (result.consumesAction()) return result;

		if (!player.isCrouching()) {
			if (player instanceof ServerPlayer serverPlayer)
				SubmarineMenu.openMenu(serverPlayer, this, hasCustomName() ? getCustomName() : NAME);

			if (!player.level.isClientSide) {
				gameEvent(GameEvent.CONTAINER_OPEN, player);
				PiglinAi.angerNearbyPiglins(player, true);
				return InteractionResult.CONSUME;
			}
		} else if (!player.level.isClientSide) {
			setMoving(!isMoving());
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void tick() {
		if (getHurtTime() > 0)
			setHurtTime(getHurtTime() - 1);

		if (getDamage() > 0F)
			setDamage(getDamage() - 1F);

		super.tick();

		handleMovement();
	}

	protected void handleMovement() {
		setOldPosAndRot();

		if (isMoving()) {
			final Direction dir = getDirection();
			final BlockPos oldPos = blockPosition();

			final double speed = .5 / 20;

			final Vec3 movement = new Vec3(dir.getStepX() * speed, 0, dir.getStepZ() * speed);

			final BlockPos nextPos = new BlockPos(position()
					.add(dir.getStepX() * getBbWidth() / 1.8, 0, dir.getStepZ() * getBbWidth() / 1.8)
					.add(movement));
			if (!oldPos.equals(nextPos)) {
				final BlockState nextState = level.getBlockState(nextPos);

				final VoxelShape coll = nextState.getCollisionShape(level, nextPos);

				if (!coll.isEmpty()) {
					final AABB bounds = coll.bounds();
					final AABB checkBounds = getBoundingBox().move(-oldPos.getX(), -oldPos.getY(), -oldPos.getZ());

					if (bounds.intersects(checkBounds))
						return;
				}
			}

			move(MoverType.SELF, movement);

			if (!level.isClientSide) {
				final Vec3 pos = position();
				final BlockPos realPos = blockPosition();
				final BlockState underState = level.getBlockState(realPos.below());

				if (underState.getBlock() instanceof SubmarineRelayBlock) {
					final SubmarinePresence presence = underState.getValue(SubmarineRelayBlock.PRESENCE);

					if (presence == SubmarinePresence.NOT_PRESENT)
						level.setBlock(realPos.below(), underState.setValue(SubmarineRelayBlock.PRESENCE, SubmarinePresence.PRESENT), 3);

					final double xOffset = pos.x - realPos.getX();
					final double zOffset = pos.z - realPos.getZ();

					if (xOffset > .49 && xOffset < .51 && zOffset > .49 && zOffset < .51) {
						// Turn in the direction of the relay.
						setYRot(underState.getValue(SubmarineRelayBlock.FACING).toYRot());

						// Correct position so that the submarine doesn't start drifting off.
						setPos(new Vec3(realPos.getX() + .5, pos.y, realPos.getZ() + .5));

						// Notify comparators and other fun things.
						level.setBlock(realPos.below(), underState.setValue(SubmarineRelayBlock.PRESENCE, SubmarinePresence.TURNING), 3);
					}
				} else if (!oldPos.equals(realPos)) {
					final BlockState pastUnderState = level.getBlockState(oldPos.below());
					if (pastUnderState.getBlock() instanceof SubmarineRelayBlock && pastUnderState.getValue(SubmarineRelayBlock.PRESENCE) != SubmarinePresence.NOT_PRESENT)
						level.setBlock(oldPos.below(), pastUnderState.setValue(SubmarineRelayBlock.PRESENCE, SubmarinePresence.NOT_PRESENT), 3);
				}
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (isInvulnerableTo(source))
			return false;

		if (!level.isClientSide && !isRemoved()) {
			setHurtDir(-getHurtDir());
			setHurtTime(10);
			setDamage(getDamage() + amount * 10.0F);
			markHurt();
			gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());

			final boolean creativeMode = source.getEntity() instanceof Player player && player.getAbilities().instabuild;

			if (creativeMode || getDamage() > 40F) {
				if (!creativeMode && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
					destroy(source);

				discard();
			}

			return true;
		}

		return true;
	}

	@Override
	public void animateHurt() {
		setHurtDir(-getHurtDir());
		setHurtTime(10);
		setDamage(getDamage() * 11F);
	}

	protected ItemStack makeSubmarineStack() {
		return new ItemStack(ISItems.SUBMARINE.get());
	}

	protected void copyData(ItemStack subStack) {
		if (!container.isEmpty())
			subStack.getOrCreateTag().put("inventory", container.createTag());

		if (hasCustomName())
			subStack.setHoverName(getCustomName());
	}

	protected void destroy(DamageSource source) {
		final ItemStack subStack = makeSubmarineStack();

		copyData(subStack);

		spawnAtLocation(subStack);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(HURT, 0);
		entityData.define(HURTDIR, 1);
		entityData.define(DAMAGE, 0F);
		entityData.define(MOVING, false);
	}

	public void setDamage(float damageTaken) {
		entityData.set(DAMAGE, damageTaken);
	}

	public float getDamage() {
		return entityData.get(DAMAGE);
	}

	public void setHurtTime(int hurtTime) {
		entityData.set(HURT, hurtTime);
	}

	public int getHurtTime() {
		return entityData.get(HURT);
	}

	public void setHurtDir(int hurtDirection) {
		entityData.set(HURTDIR, hurtDirection);
	}

	public int getHurtDir() {
		return entityData.get(HURTDIR);
	}

	public void setMoving(boolean moving) {
		entityData.set(MOVING, moving);
	}

	public boolean isMoving() {
		return entityData.get(MOVING);
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return containerCap.cast();

		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		containerCap.invalidate();
		containerCap = null;
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		containerCap = LazyOptional.of(() -> new InvWrapper(container));
	}

	public void readItemData(ItemStack stack) {
		if (!stack.hasTag()) return;

		if (stack.hasCustomHoverName())
			setCustomName(stack.getHoverName());

		if (stack.getTag().contains("inventory", Tag.TAG_LIST))
			container.fromTag(stack.getTag().getList("inventory", Tag.TAG_COMPOUND));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		container.fromTag(tag.getList("inventory", Tag.TAG_COMPOUND));
		setMoving(tag.getBoolean("moving"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.put("inventory", container.createTag());
		tag.putBoolean("moving", isMoving());
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}