package com.toxicteddie.witchywonders.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.toxicteddie.witchywonders.entity.custom.CustomPlayerEntityThick;

public class LayingDirectionSyncPacket {
    private final int entityId;
    private final Direction direction;

    public LayingDirectionSyncPacket(int entityId, Direction direction) {
        this.entityId = entityId;
        this.direction = direction;
    }

    public static void encode(LayingDirectionSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeEnum(packet.direction);
    }

    public static LayingDirectionSyncPacket decode(FriendlyByteBuf buf) {
        return new LayingDirectionSyncPacket(buf.readInt(), buf.readEnum(Direction.class));
    }

    public static void handle(LayingDirectionSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Handle packet on client side
            Minecraft minecraft = Minecraft.getInstance();
            Entity entity = minecraft.level.getEntity(packet.entityId);
            if (entity instanceof CustomPlayerEntityThick) {
                ((CustomPlayerEntityThick) entity).setDirectionFacing(packet.direction);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
