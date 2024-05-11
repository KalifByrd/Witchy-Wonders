package com.toxicteddie.witchywonders.events.powers;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.events.SoundEventHelper;
import com.toxicteddie.witchywonders.network.EntityMovePacket;
import com.toxicteddie.witchywonders.sound.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.client.event.InputEvent;
//import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber
public class TelekinesisHandler {

    //private static final Logger LOGGER = LogManager.getLogger(TelekinesisHandler.class);
    private static Entity liftedEntity = null;
    private static boolean isHoldingRightClick = false;
    private static Vec3 previousLookVec;
    private static Vec3 slingVelocity;
    private static int slingTicksRemaining = 0;
    

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Right mouse button and slot 9 check
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && mc.player.getInventory().selected == 9) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                isHoldingRightClick = true;
                previousLookVec = mc.player.getLookAngle();
                //LOGGER.info("Right click pressed. Holding: {}", isHoldingRightClick);
                if (liftedEntity == null) {
                    liftEntity(mc);
                    
                }
            } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                isHoldingRightClick = false;
                calculateSlingVelocity(mc);
                slingTicksRemaining = 10; // Smooth slinging effect duration
                //LOGGER.info("Starting smooth slinging effect.");
                
            }
        }
    }

    private static void liftEntity(Minecraft mc) {
        HitResult hitResult = mc.hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            liftedEntity = entityHitResult.getEntity();
            sendEntityUpdate(mc, liftedEntity.position(), Vec3.ZERO, false); // Lifted, no fall damage
            //LOGGER.info("Entity lifted: {}", liftedEntity);
            addSparkleEffect();
        } else {
            //LOGGER.info("No entity found to lift.");
        }
    }

    private static void calculateSlingVelocity(Minecraft mc) {
        if(liftedEntity != null) {
            Vec3 currentLookVec = mc.player.getLookAngle();
            double flingScaleFactor = 2; // Lower scale factor for smoother slinging
            slingVelocity = currentLookVec.subtract(previousLookVec).scale(flingScaleFactor);
            //LOGGER.info("Calculated sling velocity: {}", slingVelocity);
            mc.level.addParticle(ParticleTypes.EXPLOSION, liftedEntity.getX(), liftedEntity.getY(), liftedEntity.getZ(), 1.0, 0.0, 0.0);
            SoundEventHelper.stopContinuousSound();
            SoundEventHelper.playOneShotSound(mc, ModSounds.MAGIC_SOUND_END.get());
        }
    }

    @SuppressWarnings("resource")
    private static void addSparkleEffect() {
        if (liftedEntity != null) {
            for (int i = 0; i < 3; i++) {
                liftedEntity.level().addParticle(ParticleTypes.ENCHANT,
                    liftedEntity.getX() + liftedEntity.level().random.nextDouble() - 0.5,
                    liftedEntity.getY() + liftedEntity.level().random.nextDouble() * liftedEntity.getBbHeight(),
                    liftedEntity.getZ() + liftedEntity.level().random.nextDouble() - 0.5,
                    0, 0, 0);
            }
            //LOGGER.info("Sparkle effect added to entity: {}", liftedEntity);
        }
    }
    

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (liftedEntity != null && mc.player != null && isHoldingRightClick) {
                Vec3 lookVec = mc.player.getLookAngle();
                Vec3 newPosition = mc.player.position().add(lookVec.scale(3));
                if (isPathClear(mc, newPosition)) {
                    sendEntityUpdate(mc, newPosition, Vec3.ZERO, false); // Still no fall damage while moving
                    previousLookVec = lookVec;
                    //LOGGER.info("Entity move packet sent to server. New position: {}", newPosition);
                    addSparkleEffect();
                    SoundEventHelper.playContinuousSound(mc, ModSounds.MAGIC_SOUND.get());
                }
            } else if (slingTicksRemaining > 0 && liftedEntity != null) {
                Vec3 lookVec = mc.player.getLookAngle();
                Vec3 newPosition = mc.player.position().add(lookVec.scale(3));
                Vec3 velocityPerTick = slingVelocity.scale(1.0 / slingTicksRemaining);
                liftedEntity.setDeltaMovement(liftedEntity.getDeltaMovement().add(velocityPerTick));
                slingTicksRemaining--;
                if (isPathClear(mc, newPosition)) {
                    sendEntityUpdate(mc, newPosition, Vec3.ZERO, false); // Still no fall damage while moving
                    previousLookVec = lookVec;
                }
                if (slingTicksRemaining == 0) {
                    sendEntityUpdate(mc, liftedEntity.position(), liftedEntity.getDeltaMovement(), true); // Finalize slinging
                    liftedEntity = null;
                    //LOGGER.info("Smooth slinging effect completed.");
                }
            }
        }
    }

    private static void sendEntityUpdate(Minecraft mc, Vec3 position, Vec3 velocity, boolean enableFallDamage) {
        if (liftedEntity != null) {
            WitchyWonders.CHANNEL.sendToServer(new EntityMovePacket(liftedEntity.getId(), position, velocity, enableFallDamage));
        }
    }

    private static boolean isPathClear(Minecraft mc, Vec3 newPosition) {
        Vec3 playerPos = mc.player.getEyePosition(1.0F);
        HitResult hit = mc.level.clip(new ClipContext(playerPos, newPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player));
        return hit.getType() == HitResult.Type.MISS; // No obstruction if miss
    }
}
