package com.toxicteddie.witchywonders.entity.custom;

import com.toxicteddie.witchywonders.entity.animations.ModAnimationDefinitions;
import com.toxicteddie.witchywonders.path.entity.CircleAroundPlayerGoal;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomPlayerEntityThick extends PathfinderMob {
	public Direction directionFacing;
	
	public final AnimationState idleAnimationState = new AnimationState();
	
	public final AnimationState layNAnimationState = new AnimationState();
	public final AnimationState laySAnimationState = new AnimationState();
	public final AnimationState layEAnimationState = new AnimationState();
	public final AnimationState layWAnimationState = new AnimationState();
	
	private int idleAnimationTimeout = 0;
	
	public CustomPlayerEntityThick(EntityType<? extends PathfinderMob> entityType, Level world) {
		super(entityType, world);
		
		this.directionFacing = Direction.NORTH; // Default facing directio
	}
	//private static final Logger LOGGER = LogManager.getLogger();
	
    
	
	@Override
	public void tick() {
		super.tick();
		if(this.level().isClientSide()) {
			setupAnimationStates();
			
		}
	}
	
	private void setupAnimationStates() {
		this.layEAnimationState.start(this.tickCount);
        if(this.idleAnimationTimeout <= 0 ) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.layEAnimationState.start(this.tickCount);
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
	@Override
	public boolean isPushable() {
	    return false;
	}

//	@Override
//	public boolean isInvulnerableTo(DamageSource source) {
//	    return true;
//	}

	@Override
	public boolean canBeCollidedWith() {
	    return false;
	}
	@Override
    public Vec3 getDismountLocationForPassenger(LivingEntity p_19895_) {
        return null; // Ensure passengers cannot dismount
    }
	@Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            double offsetY = this.getEyeHeight() - 0.2; // Adjust this value to position the player at the head
            passenger.setPos(this.getX(), this.getY() + offsetY, this.getZ());
        }
    }

    
	public Direction getDirectionFacing() {
        return this.directionFacing;
    }
	public void setDirectionFacing(Direction direction) {
		this.directionFacing = direction;
	}


    
	
}