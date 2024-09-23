package com.toxicteddie.witchywonders.entity.custom;

import com.toxicteddie.witchywonders.entity.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class SeatEntity extends Entity {
    private static final EntityDataAccessor<BlockPos> DATA_SEAT_POS = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BLOCK_POS);

    public SeatEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true;
    }

    public SeatEntity(Level world, BlockPos pos) {
        this(ModEntities.SEAT.get(), world);
        this.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        this.entityData.set(DATA_SEAT_POS, pos);
    }

    public SeatEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(ModEntities.SEAT.get(), world);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SEAT_POS, BlockPos.ZERO);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof Player)) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public BlockPos getSeatPos() {
        return this.entityData.get(DATA_SEAT_POS);
    }
}
