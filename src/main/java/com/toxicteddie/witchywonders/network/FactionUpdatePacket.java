package com.toxicteddie.witchywonders.network;

import net.minecraft.network.FriendlyByteBuf;

public class FactionUpdatePacket {
    final String factionName;

    public FactionUpdatePacket(String factionName) {
        this.factionName = factionName;
    }

    public static void encode(FactionUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.factionName);
    }

    public static FactionUpdatePacket decode(FriendlyByteBuf buffer) {
        return new FactionUpdatePacket(buffer.readUtf(32767));
    }
}
