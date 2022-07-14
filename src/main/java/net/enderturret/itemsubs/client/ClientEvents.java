package net.enderturret.itemsubs.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.client.entity.SubmarineRenderer;
import net.enderturret.itemsubs.init.ISEntityTypes;

@EventBusSubscriber(modid = ItemSubs.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

	@SubscribeEvent
	static void registerEntityRenderers(RegisterRenderers e) {
		e.registerEntityRenderer(ISEntityTypes.SUBMARINE.get(), SubmarineRenderer::new);
	}
}