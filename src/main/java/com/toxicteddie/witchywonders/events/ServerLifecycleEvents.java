package com.toxicteddie.witchywonders.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.toxicteddie.witchywonders.network.NetworkHandler;
import com.toxicteddie.witchywonders.factions.FactionProvider;
import com.toxicteddie.witchywonders.network.FactionUpdatePacket;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class ServerLifecycleEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer serverPlayer = (ServerPlayer) event.getEntity(); // Cast to ServerPlayer
        serverPlayer.getCapability(FactionProvider.FACTION_CAP).ifPresent(faction -> {
            // Send the faction information to the player that just logged in
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                                         new FactionUpdatePacket(faction.getFaction().name()));
        });
    }
}
