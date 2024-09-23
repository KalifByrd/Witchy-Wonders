package com.toxicteddie.witchywonders.entity.custom;

import com.toxicteddie.witchywonders.path.entity.CircleAroundPlayerGoal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class MandrakeEntity extends PathfinderMob {

    public MandrakeEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()) {
            setupAnimationStates();
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

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if(this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }

        this.walkAnimation.update(f, 0.2f);
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
        this.goalSelector.addGoal(1, new CircleAroundPlayerGoal(this, 1.0D, 5.0D, 0.1D));  // Adjust speed, radius, and increment as needed
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Apply nausea effect if a player is within 5 blocks (distance squared = 25)
        this.level().players().stream()
                .filter(player -> player.distanceToSqr(this) < 25)  // Check if player is within 5 blocks
                .forEach(player -> {
                    if (!player.hasEffect(MobEffects.CONFUSION)) { // Check if the player doesn't already have nausea
                        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0)); // Apply nausea for 5 seconds
                    }
                });

        if (this.tickCount % 100 == 0) {  // Screams every 100 game ticks (5 seconds)
            this.playScreamSound();
        }
    }
    private void playScreamSound() {
        this.playSound(SoundEvents.GHAST_SCREAM, 1.0F, 1.0F); // Playing the Ghast scream sound
    }
    // @Override
    // protected void playStepSound(BlockPos pos, BlockState blockIn) {
    //     this.playSound(SoundEvents.GHAST_SCREAM, 1.0F, 1.0F);  // Assuming you have a custom scream sound
    // }

    @Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        // Ensure it does not spawn in peaceful mode
        return world.getDifficulty() != Difficulty.PEACEFUL && super.checkSpawnRules(world, spawnReason);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;  // Prevents despawning when far from the player
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GHAST_WARN;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.GHAST_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }
}
