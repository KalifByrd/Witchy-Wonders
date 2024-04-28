package com.toxicteddie.witchywonders.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;

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

    private static final ResourceLocation ENERGY_BAR_TEXTURE = new ResourceLocation("witchywonders", "textures/gui/energy_bar.png");
    private static final ResourceLocation BEAST_MARK_ICON = new ResourceLocation("witchywonders", "textures/gui/mark_of_the_beast.png");

    private static void drawMagicalEnergyBar(PoseStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        // Dimensions of each bar in the texture
        final int barWidth = 7;
        final int barHeight = 70;
        final float textureAtlasWidth = 14.0f;  // Texture atlas width in pixels
    
        // Texture coordinates
        final float backgroundXTexture = 7.0f; // Background bar starts at the beginning of the texture
        final float fillXTexture = 0.0f;       // Fill bar starts halfway through the texture (8 pixels offset)
        
        // Position on screen
        int xPosition = 10; // X position from the left edge of the screen
        int yPosition = (screenHeight - barHeight) / 2; // Center the bar vertically
    
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ENERGY_BAR_TEXTURE);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        
        // Start drawing
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        
        // Draw the background bar
        addTexturedQuad(buffer, matrixStack, xPosition, yPosition, barWidth, barHeight, backgroundXTexture, 0, barWidth, barHeight, textureAtlasWidth);
    
        // Calculate fill height based on energy level
        float energyLevel = getEnergyLevel();
        int fillHeightScaled = (int) (barHeight * energyLevel); // Scale the fill height by the energy level
    
        // Draw the fill bar
        addTexturedQuad(buffer, matrixStack, xPosition, yPosition + (barHeight - fillHeightScaled), barWidth, fillHeightScaled, fillXTexture, barHeight - fillHeightScaled, barWidth, fillHeightScaled, textureAtlasWidth);
    
        // Finish drawing the bars
        tesselator.end();
        
        // Draw the beast mark icon under the energy bar if the player is a witch
        drawBeastMarkIcon(matrixStack, xPosition, yPosition + barHeight + 5);
        RenderSystem.disableBlend();
    }
    

    private static void drawBeastMarkIcon(PoseStack matrixStack, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BEAST_MARK_ICON); // Confirm this path is correct

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc(); // Setup blending for textures with transparency

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        int iconWidth = 10;  // New smaller width
        int iconHeight = 10;  // New smaller height
        float textureAtlasSize = 10.0f;  // Ensure this matches the size of the texture file

        // Adjust the x and y to move the icon left by 5 pixels and up by 5 pixels
        x -= 2;
        y -= 3;

        addTexturedQuad(buffer, matrixStack, x, y, iconWidth, iconHeight, 0, 0, iconWidth, iconHeight, textureAtlasSize);

        tesselator.end();
        RenderSystem.disableBlend();
    }


    
    // Helper method to add a textured quad to the buffer
    private static void addTexturedQuad(BufferBuilder buffer, PoseStack matrixStack, int x, int y, int width, int height, float textureX, float textureY, int textureWidth, int textureHeight, float textureAtlasSize) {
        Matrix4f matrix = matrixStack.last().pose();
        float texX1 = textureX / textureAtlasSize;
        float texY1 = textureY / textureAtlasSize;
        float texX2 = (textureX + textureWidth) / textureAtlasSize;
        float texY2 = (textureY + textureHeight) / textureAtlasSize;
    
        buffer.vertex(matrix, x, y + height, 0.0F).uv(texX1, texY2).endVertex();
        buffer.vertex(matrix, x + width, y + height, 0.0F).uv(texX2, texY2).endVertex();
        buffer.vertex(matrix, x + width, y, 0.0F).uv(texX2, texY1).endVertex();
        buffer.vertex(matrix, x, y, 0.0F).uv(texX1, texY1).endVertex();
    }


    // Placeholder method to represent fetching the current energy level
    private static float getEnergyLevel() {
        // Implement logic to get the current energy level
        // For example, this could be from a player capability or some other state
        return 0.75F; // Example: player has 75% energy
    }



    public static void drawElementalIcons(PoseStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
    
        // Icon and texture dimensions
        final int iconWidth = 18;
        final int iconHeight = 18;
        final int textureWidth = 72; // Full texture width
        
    
        // Position the icons above the hotbar, centered horizontally
        int startX = (screenWidth - textureWidth) / 2;
        int startY = screenHeight - iconHeight - 20; // 20 pixels above the hotbar
    
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICONS);
    
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    
        for (int i = 0; i < 4; i++) {
            // Calculate the UV coordinates for the icon
            float u0 = (float) (i * iconWidth) / textureWidth;
            float u1 = (float) (i * iconWidth + iconWidth) / textureWidth;
            float v0 = 0f;
            float v1 = 1f;
    
            // Calculate the actual x position for each icon
            int x = startX + i * iconWidth;
            int y = startY;
    
            // Define vertices with UV coordinates for each icon
            buffer.vertex(matrixStack.last().pose(), x, y + iconHeight, 0f).uv(u0, v1).endVertex();
            buffer.vertex(matrixStack.last().pose(), x + iconWidth, y + iconHeight, 0f).uv(u1, v1).endVertex();
            buffer.vertex(matrixStack.last().pose(), x + iconWidth, y, 0f).uv(u1, v0).endVertex();
            buffer.vertex(matrixStack.last().pose(), x, y, 0f).uv(u0, v0).endVertex();
        }
    
        tesselator.end();
        RenderSystem.disableBlend();
    }
    
}
