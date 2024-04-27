package com.toxicteddie.witchywonders.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.toxicteddie.witchywonders.factions.FactionProvider;
import com.toxicteddie.witchywonders.factions.IFaction;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WitchHudRenderer {

    private static final Logger LOGGER = LogManager.getLogger(WitchHudRenderer.class);
    private static final ResourceLocation ICONS = new ResourceLocation("witchywonders", "textures/gui/icons.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) {
            LOGGER.info("Player instance not found.");
            return;
        }
        LOGGER.info("Rendering HUD for player: " + player.getName().getString());
        PoseStack matrixStack = new PoseStack();  // Manually creating a PoseStack

        player.getCapability(FactionProvider.FACTION_CAP).ifPresent(faction -> {
            IFaction.FactionType currentFaction = faction.getFaction();
            LOGGER.info("the players current faction is: " + currentFaction);
            if (currentFaction == IFaction.FactionType.WITCH) {
                LOGGER.info("Player is a witch. Rendering icons.");
                drawMagicalEnergyBar(matrixStack);
                drawElementalIcons(matrixStack);
            } else {
                LOGGER.info("Player is not a witch.");
            }
        });
    }

    private static void drawMagicalEnergyBar(PoseStack matrixStack) {
        // Render your energy bar here
    }

    public static void drawElementalIcons(PoseStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int x = screenWidth / 2 - 91;
        int y = screenHeight - 20;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICONS);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float textureWidth = 20;
        float textureHeight = 20;

        for (int i = 0; i < 4; i++) {
            float textureX = i * textureWidth;
            float textureY = 0;
            buffer.vertex(matrixStack.last().pose(), x + i * 20, y + textureHeight, 0).uv((textureX + textureWidth) / 256f, (textureY + textureHeight) / 256f).endVertex();
            buffer.vertex(matrixStack.last().pose(), x + i * 20 + textureWidth, y + textureHeight, 0).uv((textureX + textureWidth) / 256f, textureY / 256f).endVertex();
            buffer.vertex(matrixStack.last().pose(), x + i * 20 + textureWidth, y, 0).uv(textureX / 256f, textureY / 256f).endVertex();
            buffer.vertex(matrixStack.last().pose(), x + i * 20, y, 0).uv(textureX / 256f, (textureY + textureHeight) / 256f).endVertex();
        }

        tesselator.end();
        RenderSystem.disableBlend();
    }
}
