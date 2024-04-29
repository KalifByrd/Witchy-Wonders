package com.toxicteddie.witchywonders.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.toxicteddie.witchywonders.GameState;

@Mixin(Gui.class)
public abstract class MixinHotbarSelector {

    @Inject(method = "renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V", at = @At(value = "INVOKE", 
        target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", 
        ordinal = 0), cancellable = true)
    private void onDrawHotbarSelector(float p_283031_, GuiGraphics p_282108_, CallbackInfo ci) {
        //if (GameState.isElementalIconSelected) {
        ci.cancel();  // Only cancel if an elemental icon is selected
       // }
    }
}
