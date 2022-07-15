package net.enderturret.itemsubs.menu;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkHooks;

import net.enderturret.itemsubs.entity.SubmarineEntity;
import net.enderturret.itemsubs.init.ISMenus;

public class SubmarineMenu extends AbstractContainerMenu {

	public final SubmarineEntity submarine;

	public SubmarineMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, SubmarineEntity submarine) {
		super(menuType, containerId);
		this.submarine = submarine;
		initSlots(playerInventory);
	}

	public SubmarineMenu(int containerId, Inventory playerInventory, SubmarineEntity submarine) {
		this(ISMenus.SUBMARINE.get(), containerId, playerInventory, submarine);
	}

	public static SubmarineMenu fromData(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
		final int entityId = buf.readInt();
		final Entity submarine = playerInventory.player.level.getEntity(entityId);
		final SubmarineEntity realSub = submarine instanceof SubmarineEntity se ? se : null;

		return new SubmarineMenu(containerId, playerInventory, realSub);
	}

	public static void openMenu(ServerPlayer player, SubmarineEntity sub, Component title) {
		final SimpleMenuProvider provider = new SimpleMenuProvider(
				(containerId, playerInventory, _player) -> new SubmarineMenu(containerId, playerInventory, sub),
				title);
		NetworkHooks.openScreen(player, provider, buf -> buf.writeInt(sub.getId()));
	}

	protected void initSlots(Inventory playerInv) {
		final Container con = submarine.getContainer();

		addSlot(new Slot(con, 0, 62, 36)); // Fuel
		addSlot(new Slot(con, 1, 98, 36)); // Upgrades

		// Submarine inventory slots
		for (int y = 0; y < 2; ++y)
			for (int x = 0; x < 9; ++x)
				addSlot(new Slot(con, 2 + x + y * 9, 8 + x * 18, 72 + y * 18));

		// Player inventory slots
		for (int y = 0; y < 3; ++y)
			for (int x = 0; x < 9; ++x)
				addSlot(new Slot(playerInv, 9 + x + y * 9, 8 + x * 18, 122 + y * 18));

		// Player hotbar slots
		for (int x = 0; x < 9; ++x)
			addSlot(new Slot(playerInv, x, 8 + x * 18, 180));
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return submarine == null || submarine.getContainer().stillValid(player);
	}
}