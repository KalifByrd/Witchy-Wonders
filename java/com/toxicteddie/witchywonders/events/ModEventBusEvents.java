package com.toxicteddie.witchywonders.events;

import com.toxicteddie.witchywonders.entity.ModEntities;
import com.toxicteddie.witchywonders.entity.custom.MandrakeEntity;
import com.toxicteddie.witchywonders.entity.custom.SeatEntity;
import com.toxicteddie.witchywonders.particle.ModParticles;
import com.toxicteddie.witchywonders.particle.custom.MagicParticles;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

@Mod.EventBusSubscriber(modid = "witchywonders", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event){
        event.registerSpriteSet(ModParticles.MAGIC_PARTICLES.get(),
        MagicParticles.Provider::new);
    }

    @SubscribeEvent
    public static void registerAttribues(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MANDRAKE.get(), MandrakeEntity.createAttributes().build());
        event.put(ModEntities.CUSTOM_PLAYER_THICK.get(), MandrakeEntity.createAttributes().build());
        event.put(ModEntities.CUSTOM_PLAYER_THIN.get(), MandrakeEntity.createAttributes().build());
    }
    

    
}
