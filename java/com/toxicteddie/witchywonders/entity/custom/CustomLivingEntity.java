package com.toxicteddie.witchywonders.entity.custom;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class CustomLivingEntity extends LivingEntity {
private static final EntityDataAccessor<Optional<BlockPos>> SLEEPING_POS_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
protected CustomLivingEntity(EntityType<? extends LivingEntity> livingEntity, Level level) {
	super(livingEntity, level);
}

@Override
protected void defineSynchedData() {
	this.entityData.define(SLEEPING_POS_ID, Optional.empty());
}

@Override
public void onSyncedDataUpdated(EntityDataAccessor<?> entityData) {
  super.onSyncedDataUpdated(entityData);
  if (SLEEPING_POS_ID.equals(entityData)) {
     //if (this.level().isClientSide) {
        this.getSleepingPos().ifPresent(this::setPosToBed);
     //}
  }
}

@Override
public Optional<BlockPos> getSleepingPos() {
  return this.entityData.get(SLEEPING_POS_ID);
}
@Override
public void setSleepingPos(BlockPos p_21251_) {
	this.entityData.set(SLEEPING_POS_ID, Optional.of(p_21251_));
}
@Override
public void clearSleepingPos() {
	this.entityData.set(SLEEPING_POS_ID, Optional.empty());
}


private void setPosToBed(BlockPos p_21081_) {
  this.setPos((double)p_21081_.getX() + 0.5D, (double)p_21081_.getY() + 0.6875D, (double)p_21081_.getZ() + 0.5D);
}
}