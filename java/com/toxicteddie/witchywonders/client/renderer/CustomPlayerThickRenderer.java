package com.toxicteddie.witchywonders.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.client.model.CustomPlayerModelThick;
import com.toxicteddie.witchywonders.entity.client.ModModelLayers;
import com.toxicteddie.witchywonders.entity.custom.CustomPlayerEntityThick;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CustomPlayerThickRenderer extends MobRenderer<CustomPlayerEntityThick, CustomPlayerModelThick<CustomPlayerEntityThick>> {
	public static ResourceLocation skinLocation;

    public CustomPlayerThickRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new CustomPlayerModelThick<>(pContext.bakeLayer(ModModelLayers.CUSTOM_PLAYER_THICK_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CustomPlayerEntityThick p_115812_) {
    	if(skinLocation == null) {
    		return new ResourceLocation(WitchyWonders.MODID, "textures/entity/steve.png");
    	}
    	return skinLocation;
        
    }

    @Override
    public void render(CustomPlayerEntityThick pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
    
    
}
