package com.toxicteddie.witchywonders.entity.custom;

import com.toxicteddie.witchywonders.path.entity.CircleAroundPlayerGoal;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;

public class CustomPlayerEntityThin extends PathfinderMob {
	public CustomPlayerEntityThin(EntityType<? extends PathfinderMob> entityType, Level world) {
		super(entityType, world);
	}
	
	public final AnimationState idleAnimationState = new AnimationState();
	
	public final AnimationState layNAnimationState = new AnimationState();
	public final AnimationState laySAnimationState = new AnimationState();
	public final AnimationState layEAnimationState = new AnimationState();
	public final AnimationState layWAnimationState = new AnimationState();
	
	private int idleAnimationTimeout = 0;
	
	@Override
	public void tick() {
		super.tick();
		
		if(this.level().isClientSide() ) {
			super.tick();
			
			if(this.level().isClientSide()) {
				setupAnimationStates();
			}
		}
	}
	
	private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }
	public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)  // Setting health, can be adjusted
                .add(Attributes.MOVEMENT_SPEED, 0.55D);  // Set movement speed
    }
	
	@Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }
	
	@Override
    public boolean removeWhenFarAway(double distance) {
        return false;  // Prevents despawning when far from the player
    }
	
	@Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.PLAYER_HURT;
    }
	@Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }
	
	
	
	public void startLayingAnimation(Direction direction) {
        switch (direction) {
            case NORTH:
                this.layNAnimationState.start(this.tickCount);
                break;
            case SOUTH:
                this.laySAnimationState.start(this.tickCount);
                break;
            case EAST:
                this.layEAnimationState.start(this.tickCount);
                break;
            case WEST:
                this.layWAnimationState.start(this.tickCount);
                break;
        }
    }
}