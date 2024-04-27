package com.toxicteddie.witchywonders.network;

import com.toxicteddie.witchywonders.factions.FactionProvider;
import com.toxicteddie.witchywonders.factions.IFaction;
import net.minecraft.client.Minecraft;
import java.util.function.Supplier;
import net.minecraftforge.network.NetworkEvent;



public class ClientPacketHandler {

    @SuppressWarnings("resource")
    public static void handle(FactionUpdatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(FactionProvider.FACTION_CAP).ifPresent(faction -> {
                    try {
                        IFaction.FactionType factionType = IFaction.FactionType.valueOf(packet.factionName);
                        faction.setFaction(factionType);
                        System.out.println("Faction updated to: " + factionType); // For debugging purposes
                    } catch (IllegalArgumentException e) {
                        System.err.println("Received invalid faction type: " + packet.factionName);
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }
}
