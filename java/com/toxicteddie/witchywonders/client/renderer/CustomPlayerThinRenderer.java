package com.toxicteddie.witchywonders.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.client.model.CustomPlayerModelThin;
import com.toxicteddie.witchywonders.entity.client.ModModelLayers;
import com.toxicteddie.witchywonders.entity.custom.CustomPlayerEntityThin;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CustomPlayerThinRenderer extends MobRenderer<CustomPlayerEntityThin, CustomPlayerModelThin<CustomPlayerEntityThin>> {
	public static ResourceLocation skinLocation;

    public CustomPlayerThinRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new CustomPlayerModelThin<>(pContext.bakeLayer(ModModelLayers.CUSTOM_PLAYER_THIN_LAYER)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(CustomPlayerEntityThin p_115812_) {
    	if(skinLocation == null) {
    		return new ResourceLocation(WitchyWonders.MODID, "textures/entity/alex.png");
    	}
    	return skinLocation;
        
    }

    @Override
    public void render(CustomPlayerEntityThin pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
    
    
}
