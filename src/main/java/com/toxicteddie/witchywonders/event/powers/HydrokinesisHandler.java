package com.toxicteddie.witchywonders.event.powers;

import com.toxicteddie.witchywonders.event.SoundEventHelper;
import com.toxicteddie.witchywonders.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HydrokinesisHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }

        // Check if the right item is selected and the player is on or in water
        boolean onWater = isStandingOnWater(mc.level, player.blockPosition());
        boolean isInWater = isSubmergedInWater(mc.level, player.blockPosition());

        if (player.getInventory().selected == 10 && (onWater || isInWater)) {
            SoundEventHelper.playContinuousSound(mc, ModSounds.MAGIC_SOUND.get());
            spawnEnchantParticles(player);
            keepPlayerOnSurface(player);
            if (isInWater) {
                riseToSurface(player, mc.level);
            }
        }
    }

    private static void keepPlayerOnSurface(Player player) {
        BlockPos below = player.blockPosition().below();
        if (player.level().getFluidState(below).getType().equals(Fluids.WATER)) {
            player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
            player.setPos(player.getX(), below.getY() + 1, player.getZ());
        }
    }

    private static void riseToSurface(Player player, Level level) {
        while (level.getFluidState(player.blockPosition()).getType().equals(Fluids.WATER)) {
            double newY = player.getY() + 0.1;
            BlockPos newBlockPos = BlockPos.containing(player.getX(), newY, player.getZ());
            // Check if moving up would still leave the player submerged
            if (level.getFluidState(newBlockPos).getType().equals(Fluids.WATER)) {
                player.setPos(player.getX(), newY, player.getZ());
            } else {
                // If the next upward move makes the player not submerged, set the Y just above the water
                newY = player.blockPosition().above().getY();
                player.setPos(player.getX(), newY, player.getZ());
                break;
            }
        }
    }
    
    

    private static boolean isStandingOnWater(Level level, BlockPos pos) {
        return level.getFluidState(pos.below()).getType().equals(Fluids.WATER);
    }

    private static boolean isSubmergedInWater(Level level, BlockPos pos) {
        return level.getFluidState(pos).getType().equals(Fluids.WATER);
    }

    private static void spawnEnchantParticles(Player player) {
        Vec3 position = player.position();
        for (int i = 0; i < 5; i++) {  // Generate multiple particles around the player's feet
            double offsetX = (Math.random() - 0.5) * player.getBbWidth();
            double offsetY = 0.1;  // Slightly above the water surface
            double offsetZ = (Math.random() - 0.5) * player.getBbWidth();
            player.level().addParticle(ParticleTypes.ENCHANT,
                                     position.x + offsetX, position.y + offsetY, position.z + offsetZ,
                                     0, 0, 0);
        }
    }
}
