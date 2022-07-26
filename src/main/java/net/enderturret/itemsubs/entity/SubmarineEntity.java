package net.enderturret.itemsubs.entity;

import java.util.List;

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
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import net.enderturret.itemsubs.ISConfig;
import net.enderturret.itemsubs.SubmarineFuel;
import net.enderturret.itemsubs.block.ISubmarineBlock;
import net.enderturret.itemsubs.init.ISEntityTypes;
import net.enderturret.itemsubs.init.ISItems;
import net.enderturret.itemsubs.item.SpeedUpgradeItem;
import net.enderturret.itemsubs.menu.SubmarineMenu;
import net.enderturret.itemsubs.util.ContainerHelper2;
import net.enderturret.itemsubs.util.SlotLimitingContainer;

public class SubmarineEntity extends Entity {

	private static final EntityDataAccessor<Integer> HURT = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> HURTDIR = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.FLOAT);

	private static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.BOOLEAN);

	private static final EntityDataAccessor<Integer> BURN_TIME = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> BURN_MAX = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.INT);

	private static final EntityDataAccessor<Boolean> DECORATIVE = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Byte> LOCKED = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.BYTE);

	private static final Component NAME = Component.translatable("entity.itemsubs.submarine");

	private final SimpleContainer container = new SimpleContainer(9 * 2 + 2) {
		@Override
		public boolean stillValid(Player player) {
			return !isRemoved() && position().closerThan(player.position(), 8);
		}
		@Override
		public boolean canPlaceItem(int index, ItemStack stack) {
			if (index == 0)
				return SubmarineFuel.isValidFuel(stack);
			if (index == 1)
				return stack.getItem() instanceof SpeedUpgradeItem;
			return true;
		}
	};

	private final SlotLimitingContainer fuel = new SlotLimitingContainer(container, 0, 1);
	private final SlotLimitingContainer storage = new SlotLimitingContainer(container, 2, container.getContainerSize() - 2);

	private LazyOptional<?> containerCap = LazyOptional.of(() -> new InvWrapper(container));

	private LazyOptional<?> fuelCap = LazyOptional.of(() -> new InvWrapper(fuel));
	private LazyOptional<?> storageOnlyCap = LazyOptional.of(() -> new InvWrapper(storage));

	public SubmarineEntity(EntityType<? extends SubmarineEntity> type, Level level) {
		super(type, level);
		container.addListener(_c -> {
			if (this.level == null) return;

			final BlockState state = this.level.getBlockState(blockPosition());

			if (state.getBlock() instanceof ISubmarineBlock block)
				block.onSubmarineInventoryChanged(state, this.level, blockPosition(), this);
		});
	}

	public SubmarineEntity(Level level) {
		this(ISEntityTypes.SUBMARINE.get(), level);
	}

	public SimpleContainer getContainer() {
		return container;
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return new ItemStack(ISItems.SUBMARINE.get());
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
			if (!player.level.isClientSide) {
				if (player instanceof ServerPlayer serverPlayer)
					SubmarineMenu.openMenu(serverPlayer, this, hasCustomName() ? getCustomName() : NAME);

				gameEvent(GameEvent.CONTAINER_OPEN, player);
				PiglinAi.angerNearbyPiglins(player, true);
				return InteractionResult.CONSUME;
			}

			return InteractionResult.SUCCESS;
		} else if (!isStatusLocked()) {
			if (!player.level.isClientSide)
				setMoving(!isMoving());

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
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

	protected double getSpeed() {
		final ItemStack upgradeStack = container.getItem(1);
		final double upgradeModifier;

		// You might think an isEmpty() check here would be enough, but unfortunately commands like /data exist.
		if (!upgradeStack.isEmpty() && upgradeStack.getItem() instanceof SpeedUpgradeItem speed)
			upgradeModifier = speed.getSpeedModifier(upgradeStack);
		else
			upgradeModifier = 0;

		return .5 + upgradeModifier;
	}

	protected boolean checkMove() {
		if (!isMoving()) return false;

		if (!isDecorative() && getBurnTime() == 0) {
			if (getBurnMax() != 0) setBurnMax(0);

			final ItemStack fuel = container.getItem(0);
			if (!fuel.isEmpty()) {
				final int worth = SubmarineFuel.getBlocksTravelable(fuel);
				if (worth > 0) {
					if (fuel.hasCraftingRemainingItem())
						container.setItem(0, fuel.getCraftingRemainingItem());
					else fuel.shrink(1);
					setBurnTime(worth);
					setBurnMax(worth);
					return true;
				}
			}

			return false;
		}

		return true;
	}

	@Nullable
	protected Entity checkEntityCollision(AABB boundingBox, AABB checkBounds) {
		final List<Entity> entities = level.getEntities(this, checkBounds, e -> e.isPickable() && e.getBoundingBox().intersects(boundingBox));

		return entities.isEmpty() ? null : entities.get(0);
	}

	@Nullable
	protected Entity checkEntityCollision(BlockPos nextPos, Direction towards) {
		return checkEntityCollision(getBoundingBox().move(towards.getStepX(), towards.getStepY(), towards.getStepZ()),
				new AABB(nextPos));
	}

	@Nullable
	protected FluidState checkFluidCollision(BlockPos oldPos, BlockPos nextPos, Direction towards, BlockState nextState) {
		// Check if the submarine is about to leave or enter water.
		// This tries to avoid breaking immersion by having a submarine just yeet out of (or into) the ocean.
		// This should not affect already-levitating submarines.

		final FluidState currentFluid = level.getFluidState(oldPos);
		// This should be perfectly safe, and more performant (theoretically).
		// See LevelChunkSection#getFluidState(int, int, int).
		final FluidState nextFluid = nextState.getFluidState();

		final Fluid currentType = currentFluid.getType();
		final Fluid nextType = nextFluid.getType();

		if (currentType != nextType // If both fluids mismatch...
				&& (!currentType.isSame(nextType) // ... and both fluids are not the same kind...
						|| currentFluid.isSource() && !nextFluid.isSource())) // or the current fluid is a source block and the next fluid is not,
			return nextFluid; // Don't move forward.

		return null;
	}

	public static boolean checkDefaultBlockCollision(Level level, BlockPos pos, @Nullable Direction towards, BlockState nextState, AABB subBounds, @Nullable SubmarineEntity sub) {
		// Check if the submarine is about to collide with another block.

		final ISubmarineBlock block = nextState.getBlock() instanceof ISubmarineBlock b ? b : null;

		if (block != null) {
			// Check if the block allows or denies entry regardless of shape.
			final Boolean canEnter = block.canSubmarineEnter(nextState, level, pos, towards, sub);
			if (canEnter != null)
				return canEnter;
		}

		final VoxelShape coll;

		if (block != null)
			coll = block.getSubmarineCollisionShape(nextState, level, pos, sub);
		else
			coll = nextState.getCollisionShape(level, pos);

		if (!coll.isEmpty()) {
			final AABB bounds = coll.bounds();

			if (bounds.intersects(subBounds))
				return false;
		}

		return true;
	}

	public static boolean checkDefaultBlockCollision(Level level, BlockPos pos, @Nullable Direction towards, BlockState nextState) {
		return checkDefaultBlockCollision(level, pos, towards, nextState, ISEntityTypes.SUBMARINE.get().getAABB(0, 0, 0), null);
	}

	@Nullable
	protected BlockState checkBlockCollision(BlockPos oldPos, BlockPos nextPos, @Nullable Direction towards, BlockState nextState) {
		return checkDefaultBlockCollision(level, nextPos, towards, nextState, getBoundingBox().move(-oldPos.getX(), -oldPos.getY(), -oldPos.getZ()), this)
				? null : nextState;
	}

	protected CollisionResult checkCollision(BlockPos oldPos, BlockPos nextPos, Direction towards) {
		final BlockState nextState = level.getBlockState(nextPos);

		final FluidState f;
		if ((f = checkFluidCollision(oldPos, nextPos, towards, nextState)) != null)
			return new CollisionResult.Fluid(f);

		final Entity e;
		if ((e = checkEntityCollision(nextPos, towards)) != null)
			return new CollisionResult.Entity(e);

		final BlockState b;
		if ((b = checkBlockCollision(oldPos, nextPos, towards, nextState)) != null)
			return new CollisionResult.Block(b);

		return CollisionResult.NoCollision.INSTANCE;
	}

	private BlockPos lastCenteredPos = null;

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.EVENTS; // No sounds for now. This might change in the future.
	}

	protected void handleMovement() {
		setOldPosAndRot();

		if (checkMove()) {
			final double blocksPerSecond = getSpeed();
			double speed = blocksPerSecond / 20;

			boolean doExplosion = false;
			final boolean couldExplode = ISConfig.get().submarineExplosions() && blocksPerSecond >= 3
					&& !level.isClientSide;

			while (speed > 0) {
				final double tempMotion = Mth.clamp(speed, 0, 0.05);
				speed -= tempMotion;

				final Direction dir = getDirection();
				final BlockPos oldPos = blockPosition();

				final Vec3 movement = new Vec3(dir.getStepX() * tempMotion, 0, dir.getStepZ() * tempMotion);

				final BlockPos nextPos = new BlockPos(position()
						.add(dir.getStepX() * getBbWidth() / 1.8, 0, dir.getStepZ() * getBbWidth() / 1.8)
						.add(movement));

				if (!oldPos.equals(nextPos)) {
					final CollisionResult res = checkCollision(oldPos, nextPos, dir);
					if (res.wouldCollide())
						if (couldExplode && res instanceof CollisionResult.Entity entity
								&& entity.entity() instanceof SubmarineEntity) {}
						else
							return;
				}

				final Vec3 oldLoc = position();

				move(MoverType.SELF, movement);

				final Vec3 pos = position();

				if (couldExplode && oldLoc.equals(pos)) {
					final AABB bounds = getBoundingBox().expandTowards(movement);
					final Entity e = checkEntityCollision(bounds, bounds);
					if (e instanceof SubmarineEntity)
						doExplosion = true;
				}

				if (!level.isClientSide) {
					final BlockPos realPos = blockPosition();

					final double xOffset = pos.x - realPos.getX();
					final double zOffset = pos.z - realPos.getZ();
					final boolean centered = isCentered(xOffset, zOffset)
							&& !realPos.equals(lastCenteredPos);

					if (centered) {
						lastCenteredPos = realPos;
						setBurnTime(getBurnTime() - 1);
					}

					// Use a simple loop here to check at current position as well as below us.
					for (int yOffset = 0; yOffset == 0 || yOffset == 1; yOffset++) {
						final BlockPos offPos = realPos.below(yOffset);
						final BlockState offState = level.getBlockState(offPos);

						if (offState.getBlock() instanceof ISubmarineBlock block) {
							block.onSubmarineOver(offState, level, offPos, this, yOffset == 1);

							if (centered) {
								// Turn in the direction of the block.
								final Direction orientation = block.getOrientation(offState, level, offPos, this, yOffset == 1);
								if (orientation != null)
									setYRot(orientation.toYRot());

								// Correct position so that the submarine doesn't start drifting off.
								setPos(new Vec3(offPos.getX() + .5, pos.y, offPos.getZ() + .5));

								// Notify the block.
								block.onSubmarineDocked(offState, level, offPos, this, yOffset == 1);
							}

							break;
						}

						else if (!oldPos.equals(realPos)) {
							final BlockPos pastPos = oldPos.below(yOffset);
							final BlockState pastState = level.getBlockState(pastPos);
							if (pastState.getBlock() instanceof ISubmarineBlock block) {
								block.onSubmarineLeaving(pastState, level, pastPos, this, yOffset == 1);
								break;
							}
						}
					}
				}
			}

			if (!level.isClientSide && doExplosion) {
				remove(RemovalReason.KILLED);
				level.explode(this, getX(), getY(), getZ(), 4F, true, BlockInteraction.DESTROY);
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
			ContainerHelper2.saveAllItems(subStack.getOrCreateTag(), container, false);

		if (getBurnTime() > 0)
			subStack.getOrCreateTag().putInt("burnTime", getBurnTime());

		if (getBurnMax() > 0)
			subStack.getOrCreateTag().putInt("burnMax", getBurnMax());

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
		entityData.define(BURN_TIME, 0);
		entityData.define(BURN_MAX, 0);
		entityData.define(DECORATIVE, false);
		entityData.define(LOCKED, (byte) 0);
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

	public void setBurnTime(int burnTime) {
		entityData.set(BURN_TIME, burnTime);
	}

	public int getBurnTime() {
		return entityData.get(BURN_TIME);
	}

	public void setBurnMax(int burnMax) {
		entityData.set(BURN_MAX, burnMax);
	}

	public int getBurnMax() {
		return entityData.get(BURN_MAX);
	}

	public void setDecorative(boolean decorative) {
		entityData.set(DECORATIVE, decorative);
	}

	public boolean isDecorative() {
		return entityData.get(DECORATIVE);
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && !isDecorative()) {
			if (side == null) return containerCap.cast();
			if (side == Direction.UP) return fuelCap.cast();
			return storageOnlyCap.cast();
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		containerCap.invalidate();
		containerCap = null;
		fuelCap.invalidate();
		fuelCap = null;
		storageOnlyCap.invalidate();
		storageOnlyCap = null;
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		containerCap = LazyOptional.of(() -> new InvWrapper(container));
		fuelCap = LazyOptional.of(() -> new InvWrapper(fuel));
		storageOnlyCap = LazyOptional.of(() -> new InvWrapper(storage));
	}

	public void readItemData(ItemStack stack) {
		if (!stack.hasTag()) return;

		if (stack.hasCustomHoverName())
			setCustomName(stack.getHoverName());

		if (stack.getTag().contains("Items", Tag.TAG_LIST))
			ContainerHelper2.loadAllItems(stack.getTag(), container);

		if (stack.getTag().contains("burnTime", Tag.TAG_INT))
			setBurnTime(stack.getTag().getInt("burnTime"));

		if (stack.getTag().contains("burnMax", Tag.TAG_INT))
			setBurnTime(stack.getTag().getInt("burnMax"));

		if (stack.getTag().contains("decorative", Tag.TAG_BYTE))
			setDecorative(stack.getTag().getBoolean("decorative"));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		ContainerHelper2.loadAllItems(tag, container);
		setMoving(tag.getBoolean("moving"));
		setBurnTime(tag.getInt("burnTime"));
		setBurnMax(tag.getInt("burnMax"));
		setDecorative(tag.getBoolean("decorative"));

		if (tag.contains("locked", Tag.TAG_BYTE)) {
			final boolean val = tag.getBoolean("locked");
			setFuelLocked(val);
			setUpgradesLocked(val);
			setInventoryLocked(val);
			setStatusLocked(val);
		}
		else if (tag.contains("locked", Tag.TAG_COMPOUND)) {
			final CompoundTag lockedTag = tag.getCompound("locked");
			setFuelLocked(lockedTag.getBoolean("fuel"));
			setUpgradesLocked(lockedTag.getBoolean("upgrades"));
			setInventoryLocked(lockedTag.getBoolean("inventory"));
			setStatusLocked(lockedTag.getBoolean("status"));
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		ContainerHelper2.saveAllItems(tag, container, true);
		tag.putBoolean("moving", isMoving());
		tag.putInt("burnTime", getBurnTime());
		tag.putInt("burnMax", getBurnMax());
		tag.putBoolean("decorative", isDecorative());

		if (getLockedRaw() != 0) {
			final CompoundTag lockedTag = new CompoundTag();
			lockedTag.putBoolean("fuel", isFuelLocked());
			lockedTag.putBoolean("upgrades", isFuelLocked());
			lockedTag.putBoolean("inventory", isFuelLocked());
			lockedTag.putBoolean("status", isFuelLocked());
			tag.put("locked", lockedTag);
		}
	}

	public byte getLockedRaw() {
		return entityData.get(LOCKED);
	}

	public void setLockedRaw(byte locked) {
		entityData.set(LOCKED, locked);
	}

	public boolean isFuelLocked() {
		return (getLockedRaw() & 1) != 0;
	}

	public boolean areUpgradesLocked() {
		return (getLockedRaw() & 2) != 0;
	}

	public boolean isInventoryLocked() {
		return (getLockedRaw() & 4) != 0;
	}

	public boolean isStatusLocked() {
		return (getLockedRaw() & 8) != 0;
	}

	public void setFuelLocked(boolean locked) {
		if (locked)
			setLockedRaw((byte) (getLockedRaw() | 1));
		else
			setLockedRaw((byte) (getLockedRaw() & ~1));
	}

	public void setUpgradesLocked(boolean locked) {
		if (locked)
			setLockedRaw((byte) (getLockedRaw() | 2));
		else
			setLockedRaw((byte) (getLockedRaw() & ~2));
	}

	public void setInventoryLocked(boolean locked) {
		if (locked)
			setLockedRaw((byte) (getLockedRaw() | 4));
		else
			setLockedRaw((byte) (getLockedRaw() & ~4));
	}

	public void setStatusLocked(boolean locked) {
		if (locked)
			setLockedRaw((byte) (getLockedRaw() | 8));
		else
			setLockedRaw((byte) (getLockedRaw() & ~8));
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}

	public static boolean isCentered(double xOffset, double zOffset) {
		return xOffset > .49 && xOffset < .55 && zOffset > .49 && zOffset < .55;
	}
}