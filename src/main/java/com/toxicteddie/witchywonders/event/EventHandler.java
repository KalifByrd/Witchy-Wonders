package com.toxicteddie.witchywonders.event;

import com.toxicteddie.witchywonders.factions.FactionProvider;
import com.toxicteddie.witchywonders.factions.IFaction;
import com.toxicteddie.witchywonders.inventory.WitchInventoryCapabilityProvider;

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
            Player player = (Player) event.getObject();
            // Attaching the faction capability to all player entities
            event.addCapability(new ResourceLocation("witchywonders", "faction"), new FactionProvider());
            player.getCapability(FactionProvider.FACTION_CAP).ifPresent(faction -> {
                if (faction.getFaction() == IFaction.FactionType.WITCH) {
                    // Use WitchInventory for this player
                    event.addCapability(new ResourceLocation("witchywonders", "extra_hotbar"), new WitchInventoryCapabilityProvider((Player) event.getObject()));
                }
            });
        }
        
    }
}

