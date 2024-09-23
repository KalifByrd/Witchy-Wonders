package com.toxicteddie.witchywonders.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MortarAndPestleScreen extends AbstractContainerScreen<MortarAndPestleMenu> {
    // Reference to the default container texture
    private static final ResourceLocation CONTAINER_TEXTURE = 
            new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");
    // Reference to the default furnace texture for progress arrow
    private static final ResourceLocation FURNACE_TEXTURE = 
            new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

    public MortarAndPestleScreen(MortarAndPestleMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000; // Adjust this if you want to hide the default inventory label
        this.titleLabelY = 10000; // Adjust this if you want to hide the default title label
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Draw the container background
        RenderSystem.setShaderTexture(0, CONTAINER_TEXTURE);
        guiGraphics.blit(CONTAINER_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Draw the input slot
        guiGraphics.blit(CONTAINER_TEXTURE, x + 56, y + 36, 7 * 18, 18, 18, 18);

        // Draw the output slot
        guiGraphics.blit(CONTAINER_TEXTURE, x + 116, y + 36, 4 * 18, 18, 18, 18);

        // Draw the progress arrow
        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            RenderSystem.setShaderTexture(0, FURNACE_TEXTURE);
            guiGraphics.blit(FURNACE_TEXTURE, x + 86, y + 36, 176, 14, menu.getScaledProgress(), 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
