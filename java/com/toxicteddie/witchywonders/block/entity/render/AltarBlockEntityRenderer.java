package com.toxicteddie.witchywonders.block.entity.render;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toxicteddie.witchywonders.block.custom.AltarBlock;
import com.toxicteddie.witchywonders.block.entity.AltarBlockEntity;
import com.toxicteddie.witchywonders.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AltarBlockEntityRenderer implements BlockEntityRenderer<AltarBlockEntity> {
    public AltarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AltarBlockEntity altar, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ItemStack[] items = altar.getItems();
        BlockState[] candles = altar.getCandles();
        Direction direction = altar.getBlockState().getValue(AltarBlock.FACING);

        // Render items on the altar
        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if (!itemStack.isEmpty()) {
                poseStack.pushPose();
                // Adjust positions based on facing direction and slot
                adjustItemPosition(poseStack, direction, i, altar.getAngle(i));
                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, altar.getLevel(), 0);
                poseStack.popPose();
            }
        }

        // Render candles on the altar
        for (int i = 0; i < candles.length; i++) {
            BlockState candleState = candles[i];
            if (candleState.getBlock() != Blocks.AIR) {
                poseStack.pushPose();
                // Adjust positions based on facing direction and slot
                adjustCandlePosition(poseStack, direction, i);
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(candleState, poseStack, bufferSource, combinedLight, combinedOverlay);

                // Check if the candle is lit
                if (candleState.getValue(CandleBlock.LIT)) {
                    // Render smaller flame particle at the tip of the candle
                    if (altar.getLevel().getGameTime() % 10 == 0) {
                        double candleX = altar.getBlockPos().getX() + 0.5 + getCandleOffsetX(direction, i);
                        double candleY = altar.getBlockPos().getY() + 1.45; // Adjust height to match the tip of the candle
                        double candleZ = altar.getBlockPos().getZ() + 0.5 + getCandleOffsetZ(direction, i);
                        Minecraft.getInstance().levelRenderer.addParticle(
                            ParticleTypes.SMALL_FLAME,
                            true,
                            candleX,
                            candleY,
                            candleZ,
                            0.0,
                            0.0,
                            0.0
                        );
                        Minecraft.getInstance().level.addParticle(ParticleTypes.SMOKE, candleX, candleY, candleZ, 0.0, 0.0, 0.0);
                        // Add dynamic light source for the current lit candle
                        // int lightLevel = 15;
                        // int candleLight = lightLevel << 20 | lightLevel << 4;
                        // Minecraft.getInstance().getBlockRenderer().renderSingleBlock(candleState, poseStack, bufferSource, candleLight, combinedOverlay);
                        
                    }
                    // Add light source
                    int lightLevel = 15;
                    int candleLight = lightLevel << 20 | lightLevel << 4;
                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(candleState, poseStack, bufferSource, candleLight, combinedOverlay);
                }
                poseStack.popPose();
            }
        }
    }

    private void adjustItemPosition(PoseStack poseStack, Direction direction, int index, float angle) {
        switch (direction) {
            case NORTH:
                adjustItemPositionNorth(poseStack, index, angle);
                break;
            case SOUTH:
                adjustItemPositionSouth(poseStack, index, angle);
                break;
            case WEST:
                adjustItemPositionWest(poseStack, index, angle);
                break;
            case EAST:
                adjustItemPositionEast(poseStack, index, angle);
                break;
        }
    }

    private void adjustItemPositionNorth(PoseStack poseStack, int index, float angle) {
        switch (index) {
            case 0:
                poseStack.translate(0.5, 1.0, 0.3);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
            case 1:
                poseStack.translate(0.8, 1.1, 0.57);
                poseStack.scale(0.45f, 0.45f, 0.45f);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(angle * 45)));
                break;
            case 2:
                poseStack.translate(0.85, 1.0, 0.3);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
            case 3:
                poseStack.translate(0.2, 1.1, 0.57);
                poseStack.scale(0.45f, 0.45f, 0.45f);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(angle * 45)));
                break;
            case 4:
                poseStack.translate(0.25, 1.0, 0.3);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
        }
    }

    private void adjustItemPositionSouth(PoseStack poseStack, int index, float angle) {
        switch (index) {
            case 0:
                poseStack.translate(0.5, 1.0, 0.7);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
            case 1:
                poseStack.translate(0.2, 1.1, 0.43);
                poseStack.scale(0.45f, 0.45f, 0.45f);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(angle * 45)));
                break;
            case 2:
                poseStack.translate(0.15, 1.0, 0.7);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
            case 3:
                poseStack.translate(0.8, 1.1, 0.43);
                poseStack.scale(0.45f, 0.45f, 0.45f);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(angle * 45)));
                break;
            case 4:
                poseStack.translate(0.75, 1.0, 0.7);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
        }
    }

    private void adjustItemPositionWest(PoseStack poseStack, int index, float angle) {
        switch (index) {
            case 0:
                poseStack.translate(0.3, 1.0, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
            case 1:
                poseStack.translate(0.57, 1.1, 0.2);
                poseStack.scale(0.45f, 0.45f, 0.45f);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(angle * 45)));
                break;
            case 2:
                poseStack.translate(0.3, 1.0, 0.15);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
            case 3:
                poseStack.translate(0.57, 1.1, 0.8);
                poseStack.scale(0.45f, 0.45f, 0.45f);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(angle * 45)));
                break;
            case 4:
                poseStack.translate(0.3, 1.0, 0.75);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
        }
    }

    private void adjustItemPositionEast(PoseStack poseStack, int index, float angle) {
        switch (index) {
            case 0:
                poseStack.translate(0.7, 1.0, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
            case 1:
                poseStack.translate(0.43, 1.1, 0.8);
                poseStack.scale(0.45f, 0.45f, 0.45f);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(angle * 45)));
                break;
            case 2:
                poseStack.translate(0.7, 1.0, 0.85);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
            case 3:
                poseStack.translate(0.43, 1.1, 0.2);
                poseStack.scale(0.45f, 0.45f, 0.45f);
                poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(angle * 45)));
                break;
            case 4:
                poseStack.translate(0.7, 1.0, 0.25);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                RotationUtil.rotatePoseStack(poseStack, 90.0F, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(angle * 90)));
                break;
        }
    }

    private void adjustCandlePosition(PoseStack poseStack, Direction direction, int index) {
        switch (direction) {
            case NORTH:
                adjustCandlePositionNorth(poseStack, index);
                break;
            case SOUTH:
                adjustCandlePositionSouth(poseStack, index);
                break;
            case WEST:
                adjustCandlePositionWest(poseStack, index);
                break;
            case EAST:
                adjustCandlePositionEast(poseStack, index);
                break;
        }
    }

    private void adjustCandlePositionNorth(PoseStack poseStack, int index) {
        switch (index) {
            case 0:
                poseStack.translate(-0.4, 1.0, 0.25);
                break;
            case 1:
                poseStack.translate(0.4, 1.0, 0.25);
                break;
        }
    }

    private void adjustCandlePositionSouth(PoseStack poseStack, int index) {
        switch (index) {
            case 0:
                poseStack.translate(0.4, 1.0, -0.25);
                break;
            case 1:
                poseStack.translate(-0.4, 1.0, -0.25);
                break;
        }
    }

    private void adjustCandlePositionWest(PoseStack poseStack, int index) {
        switch (index) {
            case 0:
                poseStack.translate(0.25, 1.0, 0.4);
                break;
            case 1:
                poseStack.translate(0.25, 1.0, -0.4);
                break;
        }
    }

    private void adjustCandlePositionEast(PoseStack poseStack, int index) {
        switch (index) {
            case 0:
                poseStack.translate(-0.25, 1.0, -0.4);
                break;
            case 1:
                poseStack.translate(-0.25, 1.0, 0.4);
                break;
        }
    }

    private float getCandleOffsetX(Direction direction, int index) {
        switch (direction) {
            case NORTH:
                return index == 0 ? -0.4f : 0.4f;
            case SOUTH:
                return index == 0 ? 0.4f : -0.4f;
            case WEST:
                return 0.25f;
            case EAST:
                return -0.25f;
            default:
                return 0.0f;
        }
    }

    private float getCandleOffsetZ(Direction direction, int index) {
        switch (direction) {
            case NORTH:
                return 0.25f;
            case SOUTH:
                return -0.25f;
            case WEST:
                return index == 0 ? 0.4f : -0.4f;
            case EAST:
                return index == 0 ? -0.4f : 0.4f;
            default:
                return 0.0f;
        }
    }
}
