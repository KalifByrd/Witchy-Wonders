package com.toxicteddie.witchywonders.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetOnFirePacket {
    private final int entityId;
    private final int fireDuration;

    public SetOnFirePacket(int entityId, int fireDuration) {
        this.entityId = entityId;
        this.fireDuration = fireDuration;
    }

    public static void encode(SetOnFirePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeInt(packet.fireDuration);
    }

    public static SetOnFirePacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        int fireDuration = buf.readInt();
        return new SetOnFirePacket(entityId, fireDuration);
    }

    public static void handle(SetOnFirePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Entity entity = player.level().getEntity(packet.entityId);
                if (entity != null) {
                    entity.setSecondsOnFire(packet.fireDuration);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
