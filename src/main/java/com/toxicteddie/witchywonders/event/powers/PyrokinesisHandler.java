package com.toxicteddie.witchywonders.event.powers;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.toxicteddie.witchywonders.event.SoundEventHelper;
import com.toxicteddie.witchywonders.network.FireProjectilePacket;
import com.toxicteddie.witchywonders.network.NetworkHandler;
import com.toxicteddie.witchywonders.network.SetBlockOnFirePacket;
import com.toxicteddie.witchywonders.network.SetEntityOnFirePacket;
import com.toxicteddie.witchywonders.sound.ModSounds;

import net.minecraft.util.RandomSource;

@Mod.EventBusSubscriber
public class PyrokinesisHandler {

    private static int chargeTime = 0;
    private static final int maxChargeTime = 100;

    private static Boolean showMagicParticles = false;
    private static Boolean projectFire = false;
    private static Entity ignitedEntity = null;
    private static int ticksCount = 0;


    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || mc.level == null) return;

        if (player.getInventory().selected == 11 && event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                HitResult hitResult = mc.hitResult;
                if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                    ignitedEntity = ((EntityHitResult) hitResult).getEntity();
                    SoundEventHelper.playOneShotSound(mc, ModSounds.MAGIC_SOUND.get());
                    mc.level.addParticle(ParticleTypes.EXPLOSION, ignitedEntity.getX(), ignitedEntity.getY(), ignitedEntity.getZ(), 1.0, 0.0, 0.0);
                    NetworkHandler.INSTANCE.sendToServer(new SetEntityOnFirePacket(ignitedEntity.getId(), 5));
                    showMagicParticles = true;
                    event.setCanceled(true);
                }
                else if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                    BlockPos blockPos = blockHitResult.getBlockPos();
                    Direction direction = blockHitResult.getDirection();
                    NetworkHandler.INSTANCE.sendToServer(new SetBlockOnFirePacket(blockPos, direction));
                    SoundEventHelper.playOneShotSound(mc, ModSounds.MAGIC_SOUND.get());
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || player.getInventory().selected != 11) return;
        if(projectFire)
        {
            fireProjectile(player);
            NetworkHandler.INSTANCE.sendToServer(new FireProjectilePacket(new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ()), player.getXRot(), player.getYRot()));
            if (ticksCount < 50) {  // 50 ticks = 2.5 seconds
                ticksCount++;
            } else {
                projectFire = false;
                ticksCount = 0;  // Reset the counter
            }
        }
        if(showMagicParticles)
        {
            addSparkleEffect(null, ignitedEntity, null);
            if (ticksCount < 100) {  // 50 ticks = 5 seconds
                ticksCount++;
            } else {
                showMagicParticles = false;
                ticksCount = 0;  // Reset the counter
            }
        }
        if (mc.options.keyUse.isDown()) {
            chargeTime++;
            if (chargeTime >= maxChargeTime) {
                
                SoundEventHelper.playOneShotSound(mc, ModSounds.MAGIC_SOUND.get());
                
                
                projectFire = true;
                
                chargeTime = 0;
            }
        } else if (chargeTime > 0) {
            chargeTime = 0;
        }
    }
    private static void fireProjectile(LocalPlayer player) {
        Level world = player.level();  // Access the player's world
        Vec3 position = player.position().add(0, player.getEyeHeight(), 0);  // Start from player's eye height
        Vec3 lookVec = player.getLookAngle();  // Get the direction the player is looking
    
        int numParticles = 300;  // More particles for a more dramatic effect
        double velocity = 0.5;  // Speed of the particles
        double spread = 0.1;  // Narrower spread to simulate a focused flame blast
    
        RandomSource random = RandomSource.create();  // Random source for particle spread
    
        for (int i = 0; i < numParticles; i++) {
            double dx = lookVec.x + random.nextGaussian() * spread;
            double dy = lookVec.y + random.nextGaussian() * spread;
            double dz = lookVec.z + random.nextGaussian() * spread;
    
            // Adjust dx, dy, dz based on the lookVec for direction and add some randomness for spread
            world.addParticle(ParticleTypes.FLAME,
                              position.x, position.y, position.z,
                              dx * velocity, dy * velocity, dz * velocity);
        }
    }
    
    

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        PoseStack matrixStack = new PoseStack();  // Manually creating a PoseStack
        if (Minecraft.getInstance().player.getInventory().selected == 11) {
            renderChargeMeter(matrixStack, chargeTime, maxChargeTime);
        }
    }

    private static void renderChargeMeter(PoseStack poseStack, int charge, int maxCharge) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int x = screenWidth / 2 - 10;
        int y = screenHeight / 2 + 10;
        int width = 20;
        int height = 5;
        int chargeWidth = (int) ((float) charge / maxCharge * width);

        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        fill(poseStack, x, y, x + width, y + height, 0xFFFFFFFF);
        fill(poseStack, x, y, x + chargeWidth, y + height, 0xFFFF0000);
        RenderSystem.enableDepthTest();
    }
    private static void fill(PoseStack poseStack, int x1, int y1, int x2, int y2, int color) {
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(poseStack.last().pose(), x1, y2, 0.0F).color(color).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), x2, y2, 0.0F).color(color).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), x2, y1, 0.0F).color(color).endVertex();
        bufferBuilder.vertex(poseStack.last().pose(), x1, y1, 0.0F).color(color).endVertex();
        tessellator.end();
        RenderSystem.enableDepthTest();
    }

    

    private static void addSparkleEffect(Level worldLevel, Entity entity, BlockPos blockPos) {
        if(blockPos == null){
            Level level = entity.level();  // Directly use the entity's level which is a ServerLevel if this is server-side
            RandomSource random = level.random;
            for (int i = 0; i < 20; i++) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                level.addParticle(ParticleTypes.ENCHANT, // Use the level directly to add particles
                    entity.getX() + random.nextDouble() * entity.getBbWidth() * 2.0D - entity.getBbWidth(),
                    entity.getY() + 0.5D + random.nextDouble() * entity.getBbHeight(),
                    entity.getZ() + random.nextDouble() * entity.getBbWidth() * 2.0D - entity.getBbWidth(),
                    d0, d1, d2);
            }
        } else {
            RandomSource random = worldLevel.random;
            for (int i = 0; i < 20; i++) {
                double xOffset = random.nextDouble() - 0.5; // Offset from -0.5 to 0.5
                double yOffset = random.nextDouble() - 0.5; // Offset from -0.5 to 0.5
                double zOffset = random.nextDouble() - 0.5; // Offset from -0.5 to 0.5

                double x = blockPos.getX() + 0.5 + xOffset; // Center of the block + offset
                double y = blockPos.getY() + 0.5 + yOffset; // Center of the block + offset
                double z = blockPos.getZ() + 0.5 + zOffset; // Center of the block + offset

                double dx = xOffset * 0.02; // Spread particles slowly outward
                double dy = yOffset * 0.02; // Spread particles slowly outward
                double dz = zOffset * 0.02; // Spread particles slowly outward

                worldLevel.addParticle(ParticleTypes.ENCHANT, x, y, z, dx, dy, dz);
            }
        }
    }

}
