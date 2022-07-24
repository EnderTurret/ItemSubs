package net.enderturret.itemsubs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// https://xkcd.com/927
public class SpeedUpgradeItem extends Item {

	public SpeedUpgradeItem(Item.Properties props) {
		super(props);
	}

	public double getSpeedModifier(ItemStack stack) {
		return .25 * stack.getCount();
	}
}