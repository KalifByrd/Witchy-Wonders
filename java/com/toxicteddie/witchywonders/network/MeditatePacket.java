package com.toxicteddie.witchywonders.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class MeditatePacket {
	private final BlockPos pos;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;

    public MeditatePacket(BlockPos pos, double x, double y, double z, float yaw) {
    	this.pos = pos;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
    }

    public MeditatePacket(FriendlyByteBuf buf) {
    	this.pos = buf.readBlockPos();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
    	buf.writeBlockPos(pos);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(yaw);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                //player.teleportTo(x, y, z);
                //player.setYRot(yaw);
            	setPosToBed(player, pos);
            } else {
                Minecraft.getInstance().execute(() -> {
                    Player clientPlayer = Minecraft.getInstance().player;
                    if (clientPlayer != null) {
                        //clientPlayer.teleportTo(x, y, z);
                        //clientPlayer.setYRot(yaw);
                    	setPosToBed(clientPlayer, pos);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
    private void setPosToBed(Player player, BlockPos p_21081_) {
        //player.setPos((double)p_21081_.getX() + 0.5D, (double)p_21081_.getY() + 0.6875D, (double)p_21081_.getZ() + 0.5D);
     }
}
