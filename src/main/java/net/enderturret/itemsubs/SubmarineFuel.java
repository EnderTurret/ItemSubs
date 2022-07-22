package net.enderturret.itemsubs;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.ForgeHooks;

public class SubmarineFuel {

	public static boolean isValidFuel(ItemStack fuel) {
		return getBurnTime(fuel) > 0;
	}

	public static int getBlocksTravelable(ItemStack fuel) {
		return getBurnTime(fuel) * 5;
	}

	public static int getBurnTime(ItemStack fuel) {
		return ForgeHooks.getBurnTime(fuel, null);
	}
}