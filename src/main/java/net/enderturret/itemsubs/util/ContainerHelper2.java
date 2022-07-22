package net.enderturret.itemsubs.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class ContainerHelper2 {

	public static CompoundTag saveAllItems(CompoundTag tag, Container container, boolean saveEmpty) {
		final ListTag items = new ListTag();

		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			final ItemStack stack = container.getItem(slot);

			if (!stack.isEmpty()) {
				final CompoundTag itemTag = new CompoundTag();

				itemTag.putByte("Slot", (byte) slot);
				stack.save(itemTag);

				items.add(itemTag);
			}
		}

		if (!items.isEmpty() || saveEmpty)
			tag.put("Items", items);

		return tag;
	}

	public static void loadAllItems(CompoundTag tag, Container container) {
		final ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);

		for (int i = 0; i < items.size(); i++) {
			final CompoundTag itemTag = items.getCompound(i);
			final int slot = itemTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < container.getContainerSize())
				container.setItem(slot, ItemStack.of(itemTag));
		}
	}
}