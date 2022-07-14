package net.enderturret.itemsubs.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.entity.SubmarineEntity;

public class SubmarineRenderer extends EntityRenderer<SubmarineEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ItemSubs.MOD_ID, "textures/entity/submarine.png");

	public SubmarineRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(SubmarineEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

		
	}

	@Override
	public ResourceLocation getTextureLocation(SubmarineEntity entity) {
		return TEXTURE;
	}
}