package com.toxicteddie.witchywonders.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.toxicteddie.witchywonders.block.entity.MortarAndPestleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class MortarAndPestleRenderer implements BlockEntityRenderer<MortarAndPestleBlockEntity> {

    public MortarAndPestleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MortarAndPestleBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        IItemHandler itemHandler = blockEntity.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
        if (itemHandler != null) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    poseStack.pushPose();
                    poseStack.translate(0.5D, 1.0D, 0.5D);
                    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, combinedLight, combinedOverlay, poseStack, buffer, blockEntity.getLevel(), 0);
                    poseStack.popPose();
                }
            }
        }
    }
}
