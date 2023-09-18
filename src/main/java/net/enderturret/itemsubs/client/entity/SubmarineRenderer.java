package net.enderturret.itemsubs.client.entity;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.entity.SubmarineEntity;

@Internal
public final class SubmarineRenderer extends EntityRenderer<SubmarineEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ItemSubs.MOD_ID, "textures/entity/submarine.png");

	private final SubmarineModel model;

	public SubmarineRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new SubmarineModel(context.bakeLayer(SubmarineModel.LAYER_LOCATION));
	}

	@Override
	public void render(SubmarineEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

		poseStack.pushPose();

		final float hurtTime = entity.getHurtTime() - partialTick;

		float damage = entity.getDamage() - partialTick;
		if (damage < 0F)
			damage = 0F;

		if (hurtTime > 0F)
			poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10F * entity.getHurtDir()));

		model.setupAnim(entity, 0, 0, 0, entity.getYRot(), entity.getXRot());

		final VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

		model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(SubmarineEntity entity) {
		return TEXTURE;
	}
}