package com.toxicteddie.witchywonders.block.custom;

import net.minecraft.util.StringRepresentable;

public enum MeditationBedPart implements StringRepresentable {
    FRONT("front"),
    BACK("back");

    private final String name;

    private MeditationBedPart(String name) {
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

