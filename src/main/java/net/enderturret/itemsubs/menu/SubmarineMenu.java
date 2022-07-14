package net.enderturret.itemsubs.menu;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import net.enderturret.itemsubs.entity.SubmarineEntity;
import net.enderturret.itemsubs.init.ISMenus;

public class SubmarineMenu extends AbstractContainerMenu {

	@Nullable
	private SubmarineEntity submarine;

	public SubmarineMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, @Nullable SubmarineEntity submarine) {
		super(menuType, containerId);
		this.submarine = submarine;
	}

	public SubmarineMenu(int containerId, Inventory playerInventory, @Nullable SubmarineEntity submarine) {
		this(ISMenus.SUBMARINE.get(), containerId, playerInventory, submarine);
	}

	public SubmarineMenu(int containerId, Inventory playerInventory) {
		this(ISMenus.SUBMARINE.get(), containerId, playerInventory, null);
	}

	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return submarine == null || submarine.getContainer().stillValid(player);
	}
}