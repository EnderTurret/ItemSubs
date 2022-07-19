package net.enderturret.itemsubs.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

import net.enderturret.itemsubs.ItemSubs;
import net.enderturret.itemsubs.entity.SubmarineEntity;

public class SubmarineModel extends EntityModel<SubmarineEntity> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(ItemSubs.MOD_ID, "submarine"), "main");

	private final ModelPart root;

	public SubmarineModel(ModelPart root) {
		this.root = root.getChild("root");
	}

	public static LayerDefinition createBodyLayer() {
		final MeshDefinition mesh = new MeshDefinition();
		final PartDefinition rootDef = mesh.getRoot();

		final PartDefinition root = rootDef.addOrReplaceChild("root", CubeListBuilder.create()
				.texOffs(0, 15).addBox(0F, -11F, -5F, 5F, 7F, 8F)
				.texOffs(0, 0).addBox(-7F, -4F, -7F, 14F, 1F, 14F), PartPose.offset(0F, 0F, 0F));

		final PartDefinition wire = root.addOrReplaceChild("wire", CubeListBuilder.create()
				.texOffs(8, 11).addBox(-3F, -6F, 4F, 1F, 1F, 1F)
				.texOffs(8, 4).addBox(2F, -6F, 4F, 1F, 1F, 1F)
				.texOffs(28, 17).addBox(-1F, -5F, 1F, 1F, 1F, 3F)
				.texOffs(36, 35).addBox(-5F, -10F, 2F, 1F, 2F, 1F)
				.texOffs(0, 0).addBox(-5F, -9F, 3F, 1F, 5F, 1F)
				.texOffs(0, 7).addBox(4F, -10F, 3F, 1F, 5F, 1F)
				.texOffs(8, 0).addBox(3F, -5F, 3F, 2F, 1F, 1F)
				.texOffs(42, 9).addBox(-5F, -5F, 4F, 9F, 1F, 1F), PartPose.ZERO);

		final PartDefinition generator = root.addOrReplaceChild("generator", CubeListBuilder.create()
				.texOffs(42, 0).addBox(-4F, -5F, -5F, 3F, 1F, 8F)
				.texOffs(14, 31).addBox(-4F, -9F, -5F, 3F, 1F, 8F)
				.texOffs(18, 41).addBox(-5F, -8F, -5F, 5F, 3F, 8F)
				.texOffs(0, 30).addBox(-4F, -8F, -6F, 3F, 3F, 1F)
				.texOffs(28, 21).addBox(-4F, -8F, 3F, 3F, 3F, 1F), PartPose.ZERO);

		final PartDefinition fanSupports = root.addOrReplaceChild("fanSupports", CubeListBuilder.create()
				.texOffs(32, 28).addBox(-6F, -12F, 2F, 1F, 1F, 1F)
				.texOffs(32, 25).addBox(-6F, -10F, 4F, 1F, 1F, 1F)
				.texOffs(0, 7).addBox(-6F, -10F, -5F, 1F, 1F, 6F)
				.texOffs(14, 35).addBox(-6F, -8F, 2F, 1F, 3F, 1F)
				.texOffs(28, 25).addBox(5F, -12F, 2F, 1F, 1F, 1F)
				.texOffs(28, 28).addBox(5F, -10F, 4F, 1F, 1F, 1F)
				.texOffs(0, 0).addBox(5F, -10F, -5F, 1F, 1F, 6F)
				.texOffs(8, 7).addBox(5F, -8F, 2F, 1F, 3F, 1F)
				.texOffs(8, 2).addBox(-1F, -6F, 5F, 2F, 1F, 1F)
				.texOffs(4, 21).addBox(4F, -6F, 5F, 1F, 1F, 1F)
				.texOffs(0, 21).addBox(-5F, -6F, 5F, 1F, 1F, 1F), PartPose.ZERO);

		final PartDefinition fans = root.addOrReplaceChild("fans", CubeListBuilder.create()
				.texOffs(36, 31).addBox(-3F, -7F, 6F, 1F, 3F, 1F)
				.texOffs(32, 35).addBox(2F, -7F, 6F, 1F, 3F, 1F)
				.texOffs(28, 35).addBox(-7F, -11F, 2F, 1F, 3F, 1F)
				.texOffs(18, 35).addBox(6F, -11F, 2F, 1F, 3F, 1F)
				.texOffs(22, 40).addBox(-2F, -6F, 6F, 1F, 1F, 1F)
				.texOffs(18, 40).addBox(-4F, -6F, 6F, 1F, 1F, 1F)
				.texOffs(14, 40).addBox(1F, -6F, 6F, 1F, 1F, 1F)
				.texOffs(8, 40).addBox(3F, -6F, 6F, 1F, 1F, 1F)
				.texOffs(36, 38).addBox(-7F, -10F, 3F, 1F, 1F, 1F)
				.texOffs(8, 38).addBox(-7F, -10F, 1F, 1F, 1F, 1F)
				.texOffs(36, 28).addBox(6F, -10F, 1F, 1F, 1F, 1F)
				.texOffs(36, 25).addBox(6F, -10F, 3F, 1F, 1F, 1F), PartPose.ZERO);

		final PartDefinition fanBack = root.addOrReplaceChild("fanBack", CubeListBuilder.create()
				.texOffs(28, 31).addBox(1F, -7F, 5F, 3F, 3F, 1F)
				.texOffs(14, 31).addBox(-4F, -7F, 5F, 3F, 3F, 1F)
				.texOffs(18, 17).addBox(-6F, -11F, 1F, 1F, 3F, 3F)
				.texOffs(0, 15).addBox(5F, -11F, 1F, 1F, 3F, 3F), PartPose.ZERO);

		final PartDefinition poles = root.addOrReplaceChild("poles", CubeListBuilder.create()
				.texOffs(8, 30).addBox(5F, -12F, -6F, 1F, 7F, 1F)
				.texOffs(4, 34).addBox(-6F, -12F, -6F, 1F, 7F, 1F)
				.texOffs(0, 34).addBox(5F, -12F, 5F, 1F, 7F, 1F)
				.texOffs(36, 17).addBox(-6F, -12F, 5F, 1F, 7F, 1F), PartPose.ZERO);

		final PartDefinition rails = root.addOrReplaceChild("rails", CubeListBuilder.create()
				.texOffs(0, 30).addBox(-6F, -5F, -6F, 1F, 1F, 12F)
				.texOffs(36, 41).addBox(-5F, -13F, -6F, 10F, 1F, 1F)
				.texOffs(18, 15).addBox(-5F, -13F, 5F, 10F, 1F, 1F)
				.texOffs(14, 18).addBox(-6F, -13F, -6F, 1F, 1F, 12F)
				.texOffs(28, 28).addBox(5F, -5F, -6F, 1F, 1F, 12F)
				.texOffs(28, 15).addBox(5F, -13F, -6F, 1F, 1F, 12F), PartPose.ZERO);

		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void setupAnim(SubmarineEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		root.resetPose();
		root.loadPose(PartPose.offsetAndRotation(0, -3, 0,
				(float) Math.PI, (float) Math.PI + ((float)Math.PI / 180F) * -netHeadYaw, 0));
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}