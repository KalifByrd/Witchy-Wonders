package com.toxicteddie.witchywonders.screen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WitchsOvenScreen extends AbstractContainerScreen<WitchsOvenMenu> {
    // Reference to the default container texture
    private static final ResourceLocation CONTAINER_TEXTURE = 
            new ResourceLocation(WitchyWonders.MODID, "textures/gui/container/witchs_oven_gui.png");

    public WitchsOvenScreen(WitchsOvenMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        //this.inventoryLabelY = 10000; // Adjust this if you want to hide the default inventory label
        //this.titleLabelY = 10000; // Adjust this if you want to hide the default title label
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CONTAINER_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(CONTAINER_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
        renderFuelProgress(guiGraphics, x, y);
       
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            int arrowX = 176; // X position of the arrow in the texture
            int arrowY = 14; // Y position of the arrow in the texture
            int arrowWidth = 24; // Width of the arrow
            int arrowHeight = 17; // Height of the arrow
            int progressWidth = menu.getScaledProgress(); // Scaled progress width

            guiGraphics.blit(CONTAINER_TEXTURE, x + 88, y + 35, arrowX, arrowY, progressWidth, arrowHeight);
        }
    }
    private void renderFuelProgress(GuiGraphics guiGraphics, int x, int y) {
        if (menu.hasFuel()) {
            int fuelX = 176; // X position of the fuel in the texture
            int fuelY = 0; // Y position of the fuel in the texture
            int fuelHeight = menu.getScaledFuel(); // Scaled fuel height
            int fuelBarHeight = 14; // Full height of the fuel bar
    
            // Adjust blit method to handle the texture coordinates and height
            guiGraphics.blit(CONTAINER_TEXTURE, x + 34, y + 37 + fuelBarHeight - fuelHeight, fuelX, fuelBarHeight - fuelHeight, 14, fuelHeight);
        }
    }
    

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
