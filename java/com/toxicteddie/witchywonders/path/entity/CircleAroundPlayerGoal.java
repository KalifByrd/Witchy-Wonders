package com.toxicteddie.witchywonders.path.entity;

import java.util.EnumSet;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class CircleAroundPlayerGoal extends Goal {
    private final PathfinderMob mob;
    private Player targetPlayer;
    private final double speedModifier;
    private double radius;
    private double angle;

    public CircleAroundPlayerGoal(PathfinderMob mob, double speedModifier, double initialRadius, double radiusIncreasePerSecond) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.radius = initialRadius;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        this.targetPlayer = this.mob.level().getNearestPlayer(this.mob, 10);
        return this.targetPlayer != null;
    }

    @Override
    public boolean canContinueToUse() {
        // Ensure the player is still within a reasonable distance and is alive
        return this.targetPlayer.isAlive() && this.mob.distanceToSqr(this.targetPlayer) <= 400.0;
    }

    @Override
    public void start() {
        this.angle = 0;
    }

    @Override
    public void stop() {
        // Optionally reset the radius if you want the mob to start close to the player again on the next activation
        this.radius = Math.max(radius, 5.0); // Reset to minimum radius or keep current if it's greater
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null) {
            return;
        }

        // Increment the angle; adjust the increment rate for smoother or faster circling
        this.angle += this.speedModifier;
        if (this.angle > 360) {
            this.angle -= 360;
        }

        // Calculate the new position around the player
        double radians = Math.toRadians(this.angle);
        double x = this.targetPlayer.getX() + this.radius * Math.cos(radians);
        double z = this.targetPlayer.getZ() + this.radius * Math.sin(radians);
        this.mob.getNavigation().moveTo(x, this.mob.getY(), z, this.speedModifier);

        // Optional: Increase the radius slowly if needed, or keep it constant
        
    }
}
