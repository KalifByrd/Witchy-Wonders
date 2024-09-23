package com.toxicteddie.witchywonders.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WoodType implements StringRepresentable {
    ACACIA("acacia"),
    BAMBOO("bamboo"),
    BIRCH("birch"),
    CHERRY("cherry"),
    CRIMSON("crimson"),
    DARK_OAK("dark_oak"),
    JUNGLE("jungle"),
    MANGROVE("mangrove"),
    OAK("oak"),
    SPRUCE("spruce"),
    WARPED("warped");

    private final String name;

    private WoodType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getSerializedName();
    }
    
}
