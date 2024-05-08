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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.toxicteddie.witchywonders.network.FireProjectilePacket;
import com.toxicteddie.witchywonders.network.NetworkHandler;
import com.toxicteddie.witchywonders.network.SetOnFirePacket;
import com.toxicteddie.witchywonders.sound.ModSounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

@Mod.EventBusSubscriber
public class PyrokinesisHandler {

    private static int chargeTime = 0;
    private static final int maxChargeTime = 100;

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        if (player.getInventory().selected == 11 && event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                HitResult hitResult = mc.hitResult;
                if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) hitResult).getEntity();
                    NetworkHandler.INSTANCE.sendToServer(new SetOnFirePacket(entity.getId(), 5));
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

        if (mc.options.keyUse.isDown()) {
            chargeTime++;
            if (chargeTime >= maxChargeTime) {
                Vec3 startVec = player.getEyePosition(1.0F);
                Vec3 lookVec = player.getViewVector(1.0F);
                NetworkHandler.INSTANCE.sendToServer(new FireProjectilePacket(startVec, lookVec));
                chargeTime = 0;
            }
        } else if (chargeTime > 0) {
            chargeTime = 0;
        }
    }

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

    public static void fireProjectile(FireProjectilePacket packet, ServerPlayer player) {
        Vec3 startPosition = packet.startingPosition;
        Vec3 directionVector = packet.directionVector;

        player.level().addParticle(ParticleTypes.FLAME, startPosition.x, startPosition.y, startPosition.z, directionVector.x, directionVector.y, directionVector.z);
    }

    private static void spawnEnchantParticles(Entity entity) {
        RandomSource random = entity.level().random;
        for (int i = 0; i < 10; i++) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            entity.level().addParticle(ParticleTypes.ENCHANT, 
                entity.getX() + random.nextDouble() * entity.getBbWidth() * 2.0D - entity.getBbWidth(),
                entity.getY() + 0.5D + random.nextDouble() * entity.getBbHeight(),
                entity.getZ() + random.nextDouble() * entity.getBbWidth() * 2.0D - entity.getBbWidth(), 
                d0, d1, d2);
        }
    }

    private static void playMagicSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MAGIC_SOUND.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
