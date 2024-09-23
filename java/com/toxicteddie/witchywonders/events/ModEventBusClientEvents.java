package com.toxicteddie.witchywonders.events;

import com.toxicteddie.witchywonders.entity.ModEntities;
import com.toxicteddie.witchywonders.entity.client.MandrakeModel;
import com.toxicteddie.witchywonders.entity.client.ModModelLayers;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.entity.render.MortarAndPestleRenderer;
import com.toxicteddie.witchywonders.client.model.CustomPlayerModelThick;
import com.toxicteddie.witchywonders.client.model.CustomPlayerModelThin;
import com.toxicteddie.witchywonders.client.renderer.SeatEntityRenderer;

@Mod.EventBusSubscriber(modid = WitchyWonders.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.MANDRAKE_LAYER, MandrakeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.CUSTOM_PLAYER_THICK_LAYER, CustomPlayerModelThick::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.CUSTOM_PLAYER_THIN_LAYER, CustomPlayerModelThin::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(WitchyWonders.MORTAR_AND_PESTLE_BLOCK_ENTITY.get(), MortarAndPestleRenderer::new);
        event.registerEntityRenderer(ModEntities.SEAT.get(), SeatEntityRenderer::new);
    }
}
