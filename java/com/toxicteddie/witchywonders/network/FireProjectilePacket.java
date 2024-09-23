package com.toxicteddie.witchywonders.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import java.util.function.Supplier;

public class FireProjectilePacket {
    private final BlockPos pos;
    private final float pitch;
    private final float yaw;

    public FireProjectilePacket(BlockPos pos, float pitch, float yaw) {
        this.pos = pos;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public static void encode(FireProjectilePacket msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.pos);
        buffer.writeFloat(msg.pitch);
        buffer.writeFloat(msg.yaw);
    }

    public static FireProjectilePacket decode(FriendlyByteBuf buffer) {
        return new FireProjectilePacket(buffer.readBlockPos(), buffer.readFloat(), buffer.readFloat());
    }

    public static void handle(FireProjectilePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Server-side work
            Player player = ctx.get().getSender();
            if (player == null) return;

            Level world = player.level();
            // Calculate direction vector based on yaw and pitch
            double x = -Math.sin(Math.toRadians(msg.yaw)) * Math.cos(Math.toRadians(msg.pitch));
            double z = Math.cos(Math.toRadians(msg.yaw)) * Math.cos(Math.toRadians(msg.pitch));
            double y = -Math.sin(Math.toRadians(msg.pitch));

            // Adjust the starting position to be a few blocks in front of the player
            int startOffset = 5;  // Start the fire 5 blocks in front of the player
            BlockPos basePos = msg.pos.offset((int)(x * startOffset), (int)(y * startOffset), (int)(z * startOffset));

            // Set radius and length of the fire line
            int radius = 1;  // Radius around the line
            int length = 30;  // Length of the fire effect

            for (int i = 0; i < length; i++) {
                BlockPos currentPos = basePos.offset((int)(x * i), (int)(y * i), (int)(z * i));
                // Check and set blocks/entities on fire around currentPos
                for (BlockPos areaPos : BlockPos.betweenClosed(currentPos.offset(-radius, -radius, -radius), currentPos.offset(radius, radius, radius))) {
                    if (world.getBlockState(areaPos).isAir()) {
                        world.setBlock(areaPos, Blocks.FIRE.defaultBlockState(), 3);
                    }
                    // Check for entities and set them on fire
                    for (Entity entity : world.getEntities(null, new AABB(areaPos).inflate(radius))) {
                        entity.setSecondsOnFire(10);  // Entities burn for 10 seconds
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
