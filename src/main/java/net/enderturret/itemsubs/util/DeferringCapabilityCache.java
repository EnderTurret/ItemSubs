package net.enderturret.itemsubs.util;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public final class DeferringCapabilityCache {

	private final Map<Key, LazyOptional<?>> cache = new HashMap<>();
	private final CapabilitySupplier sup;

	public DeferringCapabilityCache(CapabilitySupplier sup) {
		this.sup = sup;
	}

	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		final Key key = new Key(cap, side);
		LazyOptional op = cache.get(key);

		if (op != null && op.isPresent())
			return op.cast();

		else { // Capability either hasn't been queried yet or is invalid. Either way, let's grab it again.
			op = sup.getCapability(cap, side);
			// Create a copy of the LazyOptional, so we can invalidate it correctly.
			op = op.lazyMap(c -> c);
			cache.put(key, op);
		}

		return op.cast();
	}

	public void invalidate() {
		cache.values().forEach(LazyOptional::invalidate);
		cache.clear();
	}

	@FunctionalInterface
	public static interface CapabilitySupplier {
		@NotNull
		public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side);
	}

	private static record Key(Capability<?> cap, Direction side) {}
}