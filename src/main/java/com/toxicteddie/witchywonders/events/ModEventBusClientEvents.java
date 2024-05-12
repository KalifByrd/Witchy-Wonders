package com.toxicteddie.witchywonders.events;

import com.toxicteddie.witchywonders.entity.client.MandrakeModel;
import com.toxicteddie.witchywonders.entity.client.ModModelLayers;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.toxicteddie.witchywonders.WitchyWonders;

@Mod.EventBusSubscriber(modid = WitchyWonders.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.MANDRAKE_LAYER, MandrakeModel::createBodyLayer);
    }
}
