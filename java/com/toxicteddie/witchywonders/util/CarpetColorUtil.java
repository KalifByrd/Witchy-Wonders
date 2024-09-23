package com.toxicteddie.witchywonders.util;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarpetColorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarpetColorUtil.class);

    public static DyeColor getCarpetColor(BlockItem carpetItem) {
        LOGGER.info("Getting carpet color for item: {}", carpetItem);

        if (carpetItem.asItem() == Items.WHITE_CARPET) return DyeColor.WHITE;
        if (carpetItem.asItem() == Items.ORANGE_CARPET) return DyeColor.ORANGE;
        if (carpetItem.asItem() == Items.MAGENTA_CARPET) return DyeColor.MAGENTA;
        if (carpetItem.asItem() == Items.LIGHT_BLUE_CARPET) return DyeColor.LIGHT_BLUE;
        if (carpetItem.asItem() == Items.YELLOW_CARPET) return DyeColor.YELLOW;
        if (carpetItem.asItem() == Items.LIME_CARPET) return DyeColor.LIME;
        if (carpetItem.asItem() == Items.PINK_CARPET) return DyeColor.PINK;
        if (carpetItem.asItem() == Items.GRAY_CARPET) return DyeColor.GRAY;
        if (carpetItem.asItem() == Items.LIGHT_GRAY_CARPET) return DyeColor.LIGHT_GRAY;
        if (carpetItem.asItem() == Items.CYAN_CARPET) return DyeColor.CYAN;
        if (carpetItem.asItem() == Items.PURPLE_CARPET) return DyeColor.PURPLE;
        if (carpetItem.asItem() == Items.BLUE_CARPET) return DyeColor.BLUE;
        if (carpetItem.asItem() == Items.BROWN_CARPET) return DyeColor.BROWN;
        if (carpetItem.asItem() == Items.GREEN_CARPET) return DyeColor.GREEN;
        if (carpetItem.asItem() == Items.RED_CARPET) return DyeColor.RED;
        if (carpetItem.asItem() == Items.BLACK_CARPET) return DyeColor.BLACK;

        LOGGER.warn("No matching DyeColor found for carpet item: {}", carpetItem);
        return null;
    }
}
