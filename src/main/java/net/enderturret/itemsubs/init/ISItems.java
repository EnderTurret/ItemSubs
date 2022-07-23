package net.enderturret.itemsubs.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.item.SpeedUpgradeItem;
import net.enderturret.itemsubs.item.SubmarineItem;

public class ISItems {

	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ItemSubs.MOD_ID);

	public static final RegistryObject<Item> SUBMARINE = REGISTRY.register("submarine", () -> new SubmarineItem(props(1)));
	public static final RegistryObject<Item> SPEED_UPGRADE = REGISTRY.register("speed_upgrade", () -> new SpeedUpgradeItem(props(16)));

	// Remember ItemBlocks?
	public static void registerBlockItems() {
		for (RegistryObject<Block> obj : ISBlocks.REGISTRY.getEntries())
			REGISTRY.register(obj.getId().getPath(), () -> new BlockItem(obj.get(), props()));
	}

	private static Item.Properties props(int stackSize) {
		return props().stacksTo(stackSize);
	}

	private static Item.Properties props() {
		return new Item.Properties().tab(CreativeModeTab.TAB_MISC);
	}
}