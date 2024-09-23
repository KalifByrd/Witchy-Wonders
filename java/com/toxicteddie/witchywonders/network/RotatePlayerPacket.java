package com.toxicteddie.witchywonders.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class RotatePlayerPacket {

    public RotatePlayerPacket() {}

    public RotatePlayerPacket(FriendlyByteBuf buf) {
        // No data to read
    }

    public void toBytes(FriendlyByteBuf buf) {
        // No data to write
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.setXRot(90.0F); // Rotate player horizontally
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void register(SimpleChannel channel, int id) {
        channel.registerMessage(id, RotatePlayerPacket.class, RotatePlayerPacket::toBytes, RotatePlayerPacket::new, RotatePlayerPacket::handle);
    }
}
