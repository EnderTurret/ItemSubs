package net.enderturret.itemsubs.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkHooks;

import net.enderturret.itemsubs.SubmarineFuel;
import net.enderturret.itemsubs.entity.SubmarineEntity;
import net.enderturret.itemsubs.init.ISMenus;

public class SubmarineMenu extends AbstractContainerMenu {

	public final SubmarineEntity submarine;
	public final SimpleContainer con;

	public SubmarineMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, SubmarineEntity submarine) {
		super(menuType, containerId);
		this.submarine = submarine;
		con = submarine.getContainer();
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

		addSlot(new Slot(con, 0, 62, 36) { // Fuel
			@Override
			public boolean mayPlace(ItemStack stack) {
				return SubmarineFuel.isValidFuel(stack);
			}
		});
		addSlot(new Slot(con, 1, 98, 36) { // Upgrades
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false; // TODO: Upgrades
			}
		});

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
		ItemStack stack = ItemStack.EMPTY;
		final Slot slot = getSlot(index);

		if (slot != null && slot.hasItem()) {
			final ItemStack slotStack = slot.getItem();
			stack = slotStack.copy();

			// If the slot is in the submarine inventory...
			if (index < con.getContainerSize()) { // We're subtracting two because reserved slots.
				// ... try to place it in the player inventory.
				// Note that slots is a list of all slots in the container.
				if (!moveItemStackTo(slotStack, con.getContainerSize() - 2, slots.size() - 9, true))
					return ItemStack.EMPTY;
			}

			// Try to put it in the fuel slot.
			else if (getSlot(0).mayPlace(slotStack) && !getSlot(0).hasItem()) {
				if (!moveItemStackTo(slotStack, 0, 1, false))
					return ItemStack.EMPTY;
			}

			// Try to put it in the submarine inventory.
			else if (!moveItemStackTo(slotStack, 2, con.getContainerSize(), false))
				return ItemStack.EMPTY;

			if (slotStack.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return stack;
	}

	@Override
	public boolean stillValid(Player player) {
		return submarine == null || submarine.getContainer().stillValid(player);
	}
}