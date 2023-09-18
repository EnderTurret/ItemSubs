package net.enderturret.itemsubs.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

/**
 * {@link Container} versions of {@link ContainerHelper} save/load methods.
 * @author EnderTurret
 */
public class ContainerHelper2 {

	public static CompoundTag saveAllItems(CompoundTag tag, Container container, boolean saveEmpty) {
		final NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

		for (int slot = 0; slot < container.getContainerSize(); slot++)
			list.set(slot, container.getItem(slot));

		ContainerHelper.saveAllItems(tag, list, saveEmpty);

		return tag;
	}

	public static void loadAllItems(CompoundTag tag, Container container) {
		final NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, list);

		for (int slot = 0; slot < list.size(); slot++)
			container.setItem(slot, list.get(slot));
	}
}