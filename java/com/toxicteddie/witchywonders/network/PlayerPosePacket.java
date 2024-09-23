package com.toxicteddie.witchywonders.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.toxicteddie.witchywonders.entity.custom.CustomLivingEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class PlayerPosePacket {
    private final BlockPos pos;
    private final boolean layDown;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final Direction direction;
    
    public PlayerPosePacket(BlockPos pos, boolean layDown, double x, double y, double z, float yaw, Direction direction) {
        this.pos = pos;
        this.layDown = layDown;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.direction = direction;
        
    }

    public PlayerPosePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.layDown = buf.readBoolean();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.direction = buf.readEnum(Direction.class);
        
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(layDown);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(yaw);
        buf.writeEnum(direction);
        
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                setPlayerPose(player, layDown, x, y, z, yaw);
                setPosToBed(player, pos);
                
                
            	
            	
            } else {
                Minecraft.getInstance().execute(() -> {
                    Player clientPlayer = Minecraft.getInstance().player;
                    if (clientPlayer != null) {
                        setPlayerPose(clientPlayer, layDown, x, y, z, yaw);
                        setPosToBed(player, pos);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private void setPlayerPose(Player player, boolean layDown, double x, double y, double z, float yaw) {
   
        player.setYRot(yaw); 


        if (layDown) {
            // Move the player to the specified position
            player.moveTo(x, y, z);

            
            BlockPos bedPos = new BlockPos(new Vec3i((int)x, (int)y, (int)z));
            BlockState bedState = player.level().getBlockState(bedPos);
            
            
            
            setPosToBed(player, bedPos);
            
            
            
            player.setDeltaMovement(Vec3.ZERO);
            player.hasImpulse = true;
            bedState.updateNeighbourShapes(player.level(), bedPos, 3);
            
            player.setPose(Pose.SLEEPING);
            player.setForcedPose(Pose.SLEEPING);
           
           
            
            
            
            
        } else {
            player.setPose(Pose.STANDING);
            player.setForcedPose(null);
            
            
        }
    }
    private void setPosToBed(Player player, BlockPos p_21081_) {
        player.setPos((double)p_21081_.getX() + 0.5D, (double)p_21081_.getY() + 0.6875D, (double)p_21081_.getZ() + 0.5D);
     }
}
