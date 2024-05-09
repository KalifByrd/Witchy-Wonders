package com.toxicteddie.witchywonders.event;

import com.toxicteddie.witchywonders.particle.ModParticles;
import com.toxicteddie.witchywonders.particle.custom.MagicParticles;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

@Mod.EventBusSubscriber(modid = "witchywonders", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event){
        event.registerSpriteSet(ModParticles.MAGIC_PARTICLES.get(),
        MagicParticles.Provider::new);
    }
}
