package net.enderturret.itemsubs.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.entity.SubmarineEntity;

public class SubmarineRenderer extends EntityRenderer<SubmarineEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ItemSubs.MOD_ID, "textures/entity/submarine.png");

	private final SubmarineModel model;

	public SubmarineRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new SubmarineModel(context.bakeLayer(SubmarineModel.LAYER_LOCATION));
	}

	@Override
	public void render(SubmarineEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

		model.setupAnim(entity, 0, 0, 0, entity.getYRot(), entity.getXRot());

		final VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

		model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
	}

	@Override
	public ResourceLocation getTextureLocation(SubmarineEntity entity) {
		return TEXTURE;
	}
}