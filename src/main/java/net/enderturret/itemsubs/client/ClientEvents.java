package net.enderturret.itemsubs.client;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.client.entity.SubmarineModel;
import net.enderturret.itemsubs.client.entity.SubmarineRenderer;
import net.enderturret.itemsubs.client.gui.SubmarineGui;
import net.enderturret.itemsubs.init.ISEntityTypes;
import net.enderturret.itemsubs.init.ISMenus;

public class ClientEvents {

	@EventBusSubscriber(modid = ItemSubs.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	static class Mod {

		@SubscribeEvent
		static void registerLayers(RegisterLayerDefinitions e) {
			e.registerLayerDefinition(SubmarineModel.LAYER_LOCATION, SubmarineModel::createBodyLayer);
		}

		@SubscribeEvent
		static void registerEntityRenderers(RegisterRenderers e) {
			e.registerEntityRenderer(ISEntityTypes.SUBMARINE.get(), SubmarineRenderer::new);
		}

		@SubscribeEvent
		static void clientSetup(FMLClientSetupEvent e) {
			e.enqueueWork(() -> {
				MenuScreens.register(ISMenus.SUBMARINE.get(), SubmarineGui::new);
			});
		}
	}

	public static IClientItemExtensions getSubmarineItemExtensions() {
		return new IClientItemExtensions() {
			@Override
			@NotNull
			public Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				return IClientItemExtensions.super.getGenericArmorModel(livingEntity, itemStack, equipmentSlot, original);
			}
		};
	}
}