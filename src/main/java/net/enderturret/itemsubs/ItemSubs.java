package net.enderturret.itemsubs;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.enderturret.itemsubs.init.ISBlocks;
import net.enderturret.itemsubs.init.ISEntityTypes;
import net.enderturret.itemsubs.init.ISItems;

@Mod(ItemSubs.MOD_ID)
public class ItemSubs {

	public static final String MOD_ID = "itemsubs";

	public ItemSubs() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		ISEntityTypes.REGISTRY.register(modBus);
		ISBlocks.REGISTRY.register(modBus);
		ISItems.REGISTRY.register(modBus);

		// Must be after all the register() calls, so that blocks have a chance to register.
		ISItems.registerBlockItems();
	}
}