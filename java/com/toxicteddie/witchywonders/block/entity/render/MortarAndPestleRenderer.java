package com.toxicteddie.witchywonders.block.entity.render;

import java.util.Random;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.entity.MortarAndPestleBlockEntity;
import com.toxicteddie.witchywonders.sound.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class MortarAndPestleRenderer implements BlockEntityRenderer<MortarAndPestleBlockEntity> {
    private static final Random random = new Random();
    public MortarAndPestleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MortarAndPestleBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        for (int i = 0; i < pBlockEntity.getItemHandler().getSlots(); i++) {
            ItemStack itemStack = pBlockEntity.getItemHandler().getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                pPoseStack.pushPose();
                pPoseStack.translate(0.5f, 0.75f, 0.5f);
                pPoseStack.scale(0.35f, 0.35f, 0.35f);
                pPoseStack.mulPose(Axis.XP.rotationDegrees(270));

                // Adjust position based on the slot index
                switch (i) {
                    case 0:
                        pPoseStack.translate(0.25f, -0.2f, -1.2f);
                        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-45));
                        pPoseStack.mulPose(Axis.XP.rotationDegrees(-60));
                        break;
                    case 1:
                        pPoseStack.translate(-0.25f, 0.2f, -1.2f);
                        pPoseStack.mulPose(Axis.ZP.rotationDegrees(45));
                        pPoseStack.mulPose(Axis.XP.rotationDegrees(60));
                        break;
                    case 2:
                        pPoseStack.translate(0.2f, 0.3f, -1.2f);
                        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-45));
                        pPoseStack.mulPose(Axis.XP.rotationDegrees(60));
                        break;
                }
                itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
                pPoseStack.popPose();
            }
        }
        // Render the pestle block item in resting position
        pPoseStack.pushPose();
        pPoseStack.translate(0.53f, 0.0f, 0.5f); // Center of the block
        pPoseStack.translate(0, 0.25, 0); // Adjust height
        pPoseStack.scale(1.0f, 1.0f, 1.0f); // Normal block scale
        pPoseStack.mulPose(Axis.YP.rotationDegrees(90)); // Adjust rotation for visual appeal

        // Animate the pestle item
        if (pBlockEntity.isAnimating() == true) {
            float animationProgress = pBlockEntity.getAnimationTime() / (float) pBlockEntity.getMaxAnimationTime();
            // Interpolate x position from 0.53 to 0.6 over the first half of the animation
            float xPos = 0.53f + Math.min(animationProgress * 4, 1) * (0.6f - 0.53f);
            pPoseStack.translate(0, 0, xPos - 0.53f); // Move pestle along x-axis

            // Twist back and forth after reaching the center
            if (animationProgress > 0.5f) {
                float twistProgress = (animationProgress - 0.5f) * 2;
                float twistAngle = (float) Math.sin(twistProgress * Math.PI * 4) * 45; // Twist back and forth faster
                pPoseStack.mulPose(Axis.YP.rotationDegrees(twistAngle));

                spawnParticles(pBlockEntity);
                
            }
            //WitchyWonders.LOGGER.info("Rendering animated pestle at position: " + pBlockEntity.getBlockPos() + " Progress: " + animationProgress);
        }

        itemRenderer.renderStatic(new ItemStack(WitchyWonders.PESTLE.get()), ItemDisplayContext.FIXED,
                getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY,
                pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
        pPoseStack.popPose();
    }

    private void spawnParticles(MortarAndPestleBlockEntity pBlockEntity) {
        BlockPos pos = pBlockEntity.getBlockPos();
        @SuppressWarnings("resource")
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;

        for (int i = 0; i < pBlockEntity.getItemHandler().getSlots(); i++) {
            ItemStack itemStack = pBlockEntity.getItemHandler().getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                for (int j = 0; j < 3; j++) { // Spawn 3 particles per tick
                    double offsetX = pos.getX() + 0.5 + random.nextGaussian() * 0.1;
                    double offsetY = pos.getY() + 0.4 + random.nextGaussian() * 0.05; // Start inside the mortar
                    double offsetZ = pos.getZ() + 0.5 + random.nextGaussian() * 0.1;
                    double speedX = random.nextGaussian() * 0.02;
                    double speedY = random.nextGaussian() * 0.02;
                    double speedZ = random.nextGaussian() * 0.02;
                    var particle = particleEngine.createParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), offsetX, offsetY, offsetZ, speedX, speedY, speedZ);
                    if (particle != null) {
                        particle.scale(0.5f); // Scale the particle to make it smaller
                    }
                }
            }
        }
    }

    private void playGrindingSound(Level level, BlockPos pos) {
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.MORTAR_AND_PESTLE_SOUND.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
