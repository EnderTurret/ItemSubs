package net.enderturret.itemsubs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.enderturret.itemsubs.ISConfig;

// https://xkcd.com/927
public class SpeedUpgradeItem extends Item {

	public SpeedUpgradeItem(Item.Properties props) {
		super(props);
	}

	public double getSpeedModifier(ItemStack stack) {
		return ISConfig.get().speedUpgradeModifier() * stack.getCount();
	}
}