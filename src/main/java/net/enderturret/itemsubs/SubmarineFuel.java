package net.enderturret.itemsubs;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.ForgeHooks;

public final class SubmarineFuel {

	public static boolean isValidFuel(ItemStack fuel) {
		return getBurnTime(fuel) > 0;
	}

	public static int getBlocksTravelable(ItemStack fuel) {
		return (int) (getBurnTime(fuel) / 200F * ISConfig.get().distanceFromFuel());
	}

	public static int getBurnTime(ItemStack fuel) {
		return ForgeHooks.getBurnTime(fuel, null);
	}
}