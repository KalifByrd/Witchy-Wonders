package com.toxicteddie.witchywonders.event;

import com.toxicteddie.witchywonders.factions.FactionProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Event handler class for attaching capabilities and handling other events.
 */
@Mod.EventBusSubscriber(modid = "witchywonders") // Replace "witchywonders" with your actual mod ID
public class EventHandler {

    /**
     * Attaches capabilities to player entities.
     * @param event The event where capabilities can be attached.
     */
    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            // Attaching the faction capability to all player entities
            event.addCapability(new ResourceLocation("witchywonders", "faction"), new FactionProvider());
        }
    }
    
    // Add other event methods as needed for your mod's functionality
}
