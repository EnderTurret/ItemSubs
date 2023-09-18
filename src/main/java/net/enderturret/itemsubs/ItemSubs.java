package net.enderturret.itemsubs;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.enderturret.itemsubs.init.ISBlockEntityTypes;
import net.enderturret.itemsubs.init.ISBlocks;
import net.enderturret.itemsubs.init.ISEntityTypes;
import net.enderturret.itemsubs.init.ISItems;
import net.enderturret.itemsubs.init.ISMenus;

@Mod(ItemSubs.MOD_ID)
@Internal
public final class ItemSubs {

	public static final String MOD_ID = "itemsubs";

	public ItemSubs() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ISConfig.SERVER_SPEC);

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		ISBlockEntityTypes.REGISTRY.register(modBus);
		ISBlocks.REGISTRY.register(modBus);
		ISEntityTypes.REGISTRY.register(modBus);
		ISItems.REGISTRY.register(modBus);
		ISMenus.REGISTRY.register(modBus);

		// Must be after all the register() calls, so that blocks have a chance to register.
		ISItems.registerBlockItems();
	}
}