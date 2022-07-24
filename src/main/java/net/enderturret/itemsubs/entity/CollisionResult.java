package net.enderturret.itemsubs.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface CollisionResult {

	public boolean wouldCollide();

	public static final class NoCollision implements CollisionResult {

		public static final NoCollision INSTANCE = new NoCollision();

		private NoCollision() {}

		@Override
		public boolean wouldCollide() {
			return false;
		}
	}

	public static record Block(BlockState state) implements CollisionResult {
		@Override
		public boolean wouldCollide() {
			return true;
		}
	}

	public static record Fluid(FluidState state) implements CollisionResult {
		@Override
		public boolean wouldCollide() {
			return true;
		}
	}

	public static record Entity(net.minecraft.world.entity.Entity entity) implements CollisionResult {
		@Override
		public boolean wouldCollide() {
			return true;
		}
	}
}