package com.toxicteddie.witchywonders.entity.client;

import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation MANDRAKE_LAYER = new ModelLayerLocation(
        new ResourceLocation(WitchyWonders.MODID, "mandrake_layer"), "main");
    public static final ModelLayerLocation CUSTOM_PLAYER_THICK_LAYER = new ModelLayerLocation(
    		new ResourceLocation(WitchyWonders.MODID, "custom_player_thick_layer"), "main");
    public static final ModelLayerLocation CUSTOM_PLAYER_THIN_LAYER = new ModelLayerLocation(
    		new ResourceLocation(WitchyWonders.MODID, "custom_player_thin_layer"), "main");
}
