package net.enderturret.itemsubs.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;

public class SlotLimitingContainer implements Container, StackedContentsCompatible {

	private final Container delegate;
	private final int slotOffset;
	private final int size;

	public SlotLimitingContainer(Container delegate, int slotOffset, int size) {
		this.delegate = delegate;
		this.slotOffset = slotOffset;
		this.size = size;
	}

	@Override
	public void clearContent() {
		for (int slot = 0; slot < size; slot++)
			setItem(slotOffset + slot, ItemStack.EMPTY);
	}

	@Override
	public int getContainerSize() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		for (int slot = 0; slot < size; slot++)
			if (!getItem(slotOffset + slot).isEmpty())
				return false;

		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot >= 0 && slot < size ? delegate.getItem(slotOffset + slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return slot >= 0 && slot < size ? delegate.removeItem(slotOffset + slot, amount) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return slot >= 0 && slot < size ? delegate.removeItemNoUpdate(slotOffset + slot) : ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot >= 0 && slot < size)
			delegate.setItem(slotOffset + slot, stack);
	}

	@Override
	public void setChanged() {
		delegate.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return delegate.stillValid(player);
	}

	@Override
	public int getMaxStackSize() {
		return delegate.getMaxStackSize();
	}

	@Override
	public void startOpen(Player player) {
		delegate.startOpen(player);
	}

	@Override
	public void stopOpen(Player player) {
		delegate.stopOpen(player);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return delegate.canPlaceItem(slotOffset + index, stack);
	}

	@Override
	public void fillStackedContents(StackedContents helper) {
		for (int slot = 0; slot < size; slot++)
			helper.accountStack(getItem(slotOffset + slot));
	}
}