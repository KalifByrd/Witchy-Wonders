package com.toxicteddie.witchywonders.entity.client;
// Made with Blockbench 4.10.0
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toxicteddie.witchywonders.entity.animations.ModAnimationDefinitions;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import com.toxicteddie.witchywonders.entity.custom.MandrakeEntity;

public class MandrakeModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart mandrake;
	private final ModelPart head;
	private final ModelPart right_branch;
	private final ModelPart left_branch;
	private final ModelPart body;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	private final ModelPart right_arm;
	private final ModelPart left_arm;

	public MandrakeModel(ModelPart root) {
		this.mandrake = root.getChild("mandrake");
		this.head = root.getChild("mandrake").getChild("head");
		this.right_branch = root.getChild("mandrake").getChild("head").getChild("right_branch");
		this.left_branch = root.getChild("mandrake").getChild("head").getChild("left_branch");
		this.body = root.getChild("mandrake").getChild("body");
		this.right_leg = root.getChild("mandrake").getChild("right_leg");
		this.left_leg = root.getChild("mandrake").getChild("left_leg");
		this.right_arm = root.getChild("mandrake").getChild("right_arm");
		this.left_arm = root.getChild("mandrake").getChild("left_arm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition mandrake = partdefinition.addOrReplaceChild("mandrake", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = mandrake.addOrReplaceChild("head", CubeListBuilder.create().texOffs(12, 8).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition right_branch = head.addOrReplaceChild("right_branch", CubeListBuilder.create().texOffs(0, 2).addBox(-0.5F, -2.0F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -2.0F, 0.0F));

		PartDefinition left_branch = head.addOrReplaceChild("left_branch", CubeListBuilder.create().texOffs(0, 8).addBox(-1.5F, -2.0F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -2.0F, 0.0F));

		PartDefinition body = mandrake.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition right_leg = mandrake.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -2.0F, 0.0F));

		PartDefinition left_leg = mandrake.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(8, 14).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -2.0F, 0.0F));

		PartDefinition right_arm = mandrake.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -5.5F, 0.0F));

		PartDefinition left_arm = mandrake.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -5.5F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);

		this.animateWalk(ModAnimationDefinitions.MANDRAKE_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.animate(((MandrakeEntity) entity).idleAnimationState, ModAnimationDefinitions.MANDRAKE_IDLE, ageInTicks, 1f);
	}

	private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
		pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
		pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

		this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		mandrake.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return mandrake;
	}

}