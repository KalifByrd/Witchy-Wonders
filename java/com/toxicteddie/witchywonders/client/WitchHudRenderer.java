package com.toxicteddie.witchywonders.client;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollingEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.toxicteddie.witchywonders.GameState;
import com.toxicteddie.witchywonders.factions.FactionProvider;
import com.toxicteddie.witchywonders.factions.IFaction;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WitchHudRenderer {

    //private static final Logger LOGGER = LogManager.getLogger(WitchHudRenderer.class);
    private static final ResourceLocation ICONS = new ResourceLocation("witchywonders", "textures/gui/icons.png");
    private static final ResourceLocation HOTBAR_SPRITE = new ResourceLocation("witchywonders", "textures/gui/hotbar.png");
 
    private static int selectedIconIndex = -1; // -1 means no custom icon is selected, using hotbar

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent event) {
        
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) {
           // LOGGER.info("Player instance not found.");
            return;
        }
        // LOGGER.info("Rendering HUD for player: " + player.getName().getString());
        PoseStack matrixStack = new PoseStack();  // Manually creating a PoseStack

        player.getCapability(FactionProvider.FACTION_CAP).ifPresent(faction -> {
            IFaction.FactionType currentFaction = faction.getFaction();
           // LOGGER.info("the players current faction is: " + currentFaction);
            if (currentFaction == IFaction.FactionType.WITCH) {
              //  LOGGER.info("Player is a witch. Rendering icons.");
                drawPowerHotbarBackground(matrixStack);
                drawMagicalEnergyBar(matrixStack);
                drawElementalIcons(matrixStack);
                renderHotbarSelector(matrixStack);
                
        
            } else {
               // LOGGER.info("Player is not a witch.");
            }
        });
        
        if (player == null || selectedIconIndex == -1){
            GameState.isElementalIconSelected = false;
            return;
        }
        //event.setCanceled(true);  // Cancels rendering the default hotbar
        GameState.isElementalIconSelected = true;
        
       
        drawElementalIcons(matrixStack);
        player.getInventory().setPickedItem(ItemStack.EMPTY);  // Ensure no item is displayed in hand
    }
    private static void renderHotbarSelector(PoseStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
    
        // Assuming each hotbar slot is 20 pixels wide
        final int slotWidth = 20;
        int hotbarWidth = 9 * slotWidth;
        int startX = (screenWidth - hotbarWidth) / 2 - 2;
    
        // Selector position based on currently selected slot
        int selectorX = startX + mc.player.getInventory().selected * slotWidth;
        int selectorY = screenHeight - 23; // Position at the bottom of the screen
    
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/gui/widgets.png"));
        
        // UV coordinates for the selector, adjusted if necessary
        float uvX1 = 0.0f;
        float uvY1 = 22.0f / 256.0f;
        float uvX2 = 24.0f / 256.0f;
        float uvY2 = (22.0f + 22.0f) / 256.0f;
        // Drawing the selector
        matrixStack.pushPose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrixStack.last().pose(), selectorX, selectorY + 22, 0.0F).uv(uvX1, uvY2).endVertex();
        buffer.vertex(matrixStack.last().pose(), selectorX + 24, selectorY + 22, 0.0F).uv(uvX2, uvY2).endVertex();
        buffer.vertex(matrixStack.last().pose(), selectorX + 24, selectorY, 0.0F).uv(uvX2, uvY1).endVertex();
        buffer.vertex(matrixStack.last().pose(), selectorX, selectorY, 0.0F).uv(uvX1, uvY1).endVertex();
        tesselator.end();
        matrixStack.popPose();
        RenderSystem.disableBlend();
    }
    
    
    private static void drawPowerHotbarBackground(PoseStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        // Dimensions and position
        final int iconWidth = 20; // Same width as the hotbar slots
        final int iconHeight = 22; // Same height for consistency
        int hotbarWidth = 9 * iconWidth; // Width of the default hotbar
        int startX = (screenWidth - hotbarWidth) / 2 + hotbarWidth; // Start right after the hotbar, adding a small gap
        int startY = screenHeight - iconHeight; // Lowering the bar to align just above the default hotbar, reduce further if needed
    
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, HOTBAR_SPRITE);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    
        // Assuming the sprite sheet has individual icons laid out horizontally
        int textureWidth = 80; // Total width of the texture containing all icons
        int singleIconTextureWidth = textureWidth / 4; // Width of each icon in the texture
    
        // Draw each additional slot without gaps
        for (int i = 0; i < 4; i++) {
            float u0 = (float) (i * singleIconTextureWidth) / textureWidth; // Starting U coordinate
            float u1 = (float) (i * singleIconTextureWidth + singleIconTextureWidth) / textureWidth; // Ending U coordinate
            float v0 = 0.0f; // Starting V coordinate (top of the texture)
            float v1 = 1.0f; // Ending V coordinate (bottom of the texture)
    
            int x = startX + i * iconWidth; // Position each slot next to the other without any gap
            int y = startY;
    
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f); // Normal coloring
    
            matrixStack.pushPose();
            buffer.vertex(matrixStack.last().pose(), x, y + iconHeight, 0f).uv(u0, v1).endVertex();
            buffer.vertex(matrixStack.last().pose(), x + iconWidth, y + iconHeight, 0f).uv(u1, v1).endVertex();
            buffer.vertex(matrixStack.last().pose(), x + iconWidth, y, 0f).uv(u1, v0).endVertex();
            buffer.vertex(matrixStack.last().pose(), x, y, 0f).uv(u0, v0).endVertex();
            matrixStack.popPose();
        }
        
        tesselator.end();
        RenderSystem.disableBlend();
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

        // Icon dimensions
        final int iconWidth = 18;
        final int iconHeight = 18;
        
        // Assuming each hotbar slot is approximately 20 pixels wide and there are 9 slots
        int hotbarWidth = 9 * 20; // Total width of the hotbar
        int startX = (screenWidth + hotbarWidth) / 2 + 1; // Start right after the hotbar, adding a little gap
        int startY = screenHeight - iconHeight - 2; // Align with the bottom of the screen, like the hotbar
        int spacing = 2; // Spacing between each icon

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICONS);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < 4; i++) {
            float u0 = (float) (i * iconWidth) / 72; // textureWidth is the full width of the icon strip
            float u1 = (float) (i * iconWidth + iconWidth) / 72;
            float v0 = 0f;
            float v1 = 1f;

            int x = startX + i * (iconWidth + spacing); // Position each icon to the right with additional spacing
            int y = startY;

            matrixStack.pushPose();
            buffer.vertex(matrixStack.last().pose(), x, y + iconHeight, 0f).uv(u0, v1).endVertex();
            buffer.vertex(matrixStack.last().pose(), x + iconWidth, y + iconHeight, 0f).uv(u1, v1).endVertex();
            buffer.vertex(matrixStack.last().pose(), x + iconWidth, y, 0f).uv(u1, v0).endVertex();
            buffer.vertex(matrixStack.last().pose(), x, y, 0f).uv(u0, v0).endVertex();
            matrixStack.popPose();
        }

        tesselator.end();
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS) return;  // Only act on key presses

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !isWitch(player)) return; // Check if player is a witch

        int key = event.getKey();
        boolean isCustomKey = false;

        // Mapping the keys directly to slot indices
        switch (key) {
            case GLFW.GLFW_KEY_1:
            case GLFW.GLFW_KEY_2:
            case GLFW.GLFW_KEY_3:
            case GLFW.GLFW_KEY_4:
            case GLFW.GLFW_KEY_5:
            case GLFW.GLFW_KEY_6:
            case GLFW.GLFW_KEY_7:
            case GLFW.GLFW_KEY_8:
            case GLFW.GLFW_KEY_9:
                mc.player.getInventory().selected = key - GLFW.GLFW_KEY_1;
                selectedIconIndex = -1;
                break;
            case GLFW.GLFW_KEY_0:
                mc.player.getInventory().selected = 9;
                selectedIconIndex = -1;
                break;
            case GLFW.GLFW_KEY_MINUS:
                mc.player.getInventory().selected = 10;
                selectedIconIndex = -1;
                break;
            case GLFW.GLFW_KEY_EQUAL:
                mc.player.getInventory().selected = 11;
                selectedIconIndex = -1;
                break;
            case GLFW.GLFW_KEY_GRAVE_ACCENT:
                mc.player.getInventory().selected = 12;
                selectedIconIndex = -1;
                break;
            default:
                return;  // If not a mapped key, exit without changing
        }

        if (!isCustomKey) {
            selectedIconIndex = -1;
        }
    }



    @SubscribeEvent
    public static void onMouseScroll(MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (mc.screen instanceof InventoryScreen || player == null|| !isWitch(player)) return; // Ignore if in inventory screen or player is null

        int currentSlot = player.getInventory().selected;
        double scrollDelta = event.getScrollDelta();

        if (scrollDelta > 0) { // Scrolling up
            currentSlot++;
            if (currentSlot >= 13) { // Check if it goes beyond the 12th slot (index 12)
                currentSlot = 0; // Wrap to the first slot
            }
        } else if (scrollDelta < 0) { // Scrolling down
            currentSlot--;
            if (currentSlot < 0) { // Check if it goes below the first slot (index 0)
                currentSlot = 12; // Wrap to the last slot
            }
        }

        player.getInventory().selected = currentSlot; // Set the current slot, this handles all 13 slots now
        event.setCanceled(true); // Prevent default scrolling behavior
    }
    private static boolean isWitch(Player player) {
        return player.getCapability(FactionProvider.FACTION_CAP).map(faction -> faction.getFaction() == IFaction.FactionType.WITCH).orElse(false);
    }

    





}
