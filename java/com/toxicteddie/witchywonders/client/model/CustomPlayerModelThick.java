package com.toxicteddie.witchywonders.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toxicteddie.witchywonders.entity.animations.ModAnimationDefinitions;
import com.toxicteddie.witchywonders.entity.custom.CustomPlayerEntityThick;
import com.toxicteddie.witchywonders.entity.custom.MandrakeEntity;
import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CustomPlayerModelThick<T extends Entity> extends HierarchicalModel<T> {
	
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	//public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "customplayer_thick"), "main");
	//public final Direction layingDirection;
	private final ModelPart Player;
	private final ModelPart Head;
	private final ModelPart Body;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;

	public CustomPlayerModelThick(ModelPart root) {
		this.Player = root.getChild("Player");
		this.Head = root.getChild("Player").getChild("Head");
		this.Body = root.getChild("Player").getChild("Body");
		this.RightArm = root.getChild("Player").getChild("RightArm");
		this.LeftArm = root.getChild("Player").getChild("LeftArm");
		this.RightLeg = root.getChild("Player").getChild("RightLeg");
		this.LeftLeg = root.getChild("Player").getChild("LeftLeg");
		//this.layingDirection = null;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Player = partdefinition.addOrReplaceChild("Player", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Head = Player.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition Body = Player.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition RightArm = Player.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, -22.0F, 0.0F));

		PartDefinition LeftArm = Player.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, -22.0F, 0.0F));

		PartDefinition RightLeg = Player.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.9F, -12.0F, 0.0F));

		PartDefinition LeftLeg = Player.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(1.9F, -12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);
		
		// Call rotatePlayer with the correct direction
	    if (entity instanceof CustomPlayerEntityThick) {
	    	WitchyWonders.LOGGER.info("This is the entity " + ((CustomPlayerEntityThick) entity) );
	    	WitchyWonders.LOGGER.info("This is the direction: " + ((CustomPlayerEntityThick) entity).getDirectionFacing());
	        this.rotatePlayer(((CustomPlayerEntityThick) entity).getDirectionFacing());
	    }
	    
		this.animate(((CustomPlayerEntityThick) entity).idleAnimationState, ModAnimationDefinitions.PLAYER_IDLE, ageInTicks);
		this.animate(((CustomPlayerEntityThick) entity).layNAnimationState, ModAnimationDefinitions.PLAYER_LAY_NORTH, ageInTicks);
		this.animate(((CustomPlayerEntityThick) entity).laySAnimationState, ModAnimationDefinitions.PLAYER_LAY_SOUTH, ageInTicks);
		this.animate(((CustomPlayerEntityThick) entity).layEAnimationState, ModAnimationDefinitions.PLAYER_LAY_EAST, ageInTicks);
		this.animate(((CustomPlayerEntityThick) entity).layWAnimationState, ModAnimationDefinitions.PLAYER_LAY_WEST, ageInTicks);
		
	    
	}
	public void rotatePlayer(Direction direction) {
	    switch (direction) {
	        case NORTH:
	            this.Player.yRot = (float) Math.PI; // 180 degrees horizontal
	            this.Player.xRot = -(float) Math.PI / 2F; // Lying horizontally facing up
	            break;
	        case SOUTH:
	            this.Player.yRot = 0.0F; // Facing south horizontally
	            this.Player.xRot = -(float) Math.PI / 2F; // Lying horizontally facing up
	            break;
	        case WEST:
	            this.Player.yRot = (float) Math.PI / 2F; // Facing west horizontally
	            this.Player.xRot = -(float) Math.PI / 2F; // Lying horizontally facing up
	            break;
	        case EAST:
	            this.Player.yRot = -(float) Math.PI / 2F; // Facing east horizontally
	            this.Player.xRot = -(float) Math.PI / 2F; // Lying horizontally facing up
	            break;
	    }
	    System.out.println("Rotation applied: yRot = " + this.Player.yRot + ", xRot = " + this.Player.xRot);
	}


	private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
		pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
		pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

		this.Head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
		this.Head.xRot = pHeadPitch * ((float)Math.PI / 180F);
	}


	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Player.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
	
	@Override
	public ModelPart root() {
		return Player;
	}
}