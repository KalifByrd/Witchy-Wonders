package com.toxicteddie.witchywonders.block.custom;

import net.minecraft.util.StringRepresentable;

public enum AltarPart implements StringRepresentable {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right"),
    FULL("full");

    private final String name;

    private AltarPart(String name) {
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

