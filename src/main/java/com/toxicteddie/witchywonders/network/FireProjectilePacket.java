package com.toxicteddie.witchywonders.network;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FireProjectilePacket {
    public final Vec3 startingPosition;
    public final Vec3 directionVector;

    public FireProjectilePacket(Vec3 start, Vec3 direction) {
        this.startingPosition = start;
        this.directionVector = direction;
    }

    public static void encode(FireProjectilePacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.startingPosition.x);
        buf.writeDouble(packet.startingPosition.y);
        buf.writeDouble(packet.startingPosition.z);
        buf.writeDouble(packet.directionVector.x);
        buf.writeDouble(packet.directionVector.y);
        buf.writeDouble(packet.directionVector.z);
    }

    public static FireProjectilePacket decode(FriendlyByteBuf buf) {
        Vec3 start = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 direction = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        return new FireProjectilePacket(start, direction);
    }

    public static void handle(FireProjectilePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Vec3 startPosition = packet.startingPosition.add(packet.directionVector.scale(2.0)); // Example adjustment
                player.level().addParticle(ParticleTypes.FLAME, startPosition.x, startPosition.y, startPosition.z, 0, 0, 0);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
