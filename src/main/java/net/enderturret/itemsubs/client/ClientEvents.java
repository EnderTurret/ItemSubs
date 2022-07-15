package net.enderturret.itemsubs.client;

import net.minecraft.client.gui.screens.MenuScreens;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.client.entity.SubmarineRenderer;
import net.enderturret.itemsubs.client.gui.SubmarineGui;
import net.enderturret.itemsubs.init.ISEntityTypes;
import net.enderturret.itemsubs.init.ISMenus;

@EventBusSubscriber(modid = ItemSubs.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

	@SubscribeEvent
	static void registerEntityRenderers(RegisterRenderers e) {
		e.registerEntityRenderer(ISEntityTypes.SUBMARINE.get(), SubmarineRenderer::new);
	}

	@EventBusSubscriber(modid = ItemSubs.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	static class Mod {

		@SubscribeEvent
		static void clientSetup(FMLClientSetupEvent e) {
			e.enqueueWork(() -> {
				MenuScreens.register(ISMenus.SUBMARINE.get(), SubmarineGui::new);
			});
		}
	}
}