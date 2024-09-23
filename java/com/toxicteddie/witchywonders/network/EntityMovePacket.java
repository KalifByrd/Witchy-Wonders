// EntityMovePacket.java
package com.toxicteddie.witchywonders.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityMovePacket {
    private final int entityId;
    private final Vec3 newPos;
    private final Vec3 velocity;
    private final boolean enableFallDamage;

    public EntityMovePacket(int entityId, Vec3 newPos, Vec3 velocity, boolean enableFallDamage) {
        this.entityId = entityId;
        this.newPos = newPos;
        this.velocity = velocity;
        this.enableFallDamage = enableFallDamage;
    }

    public EntityMovePacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.newPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.velocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.enableFallDamage = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeDouble(newPos.x);
        buf.writeDouble(newPos.y);
        buf.writeDouble(newPos.z);
        buf.writeDouble(velocity.x);
        buf.writeDouble(velocity.y);
        buf.writeDouble(velocity.z);
        buf.writeBoolean(enableFallDamage);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(entityId);
                if (entity != null) {
                    entity.moveTo(newPos.x, newPos.y, newPos.z, entity.getYRot(), entity.getXRot());
                    entity.setDeltaMovement(velocity);
                    if (enableFallDamage) {
                        entity.fallDistance = 3.0F; // Setting fall distance
                    } else {
                        entity.fallDistance = 0.0F; // Reset fall distance while levitating
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
