package com.toxicteddie.witchywonders.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.entity.custom.MandrakeEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MandrakeRenderer extends MobRenderer<MandrakeEntity, MandrakeModel<MandrakeEntity>> {

    public MandrakeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new MandrakeModel<>(pContext.bakeLayer(ModModelLayers.MANDRAKE_LAYER)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(MandrakeEntity p_115812_) {
        return new ResourceLocation(WitchyWonders.MODID, "textures/entity/mandrake.png");
    }

    @Override
    public void render(MandrakeEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
    
    
}
