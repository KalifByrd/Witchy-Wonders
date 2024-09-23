package com.toxicteddie.witchywonders.block.custom;

import net.minecraft.util.StringRepresentable;

public enum ClothColor implements StringRepresentable{
    BLANK("blank"),
    WHITE("white"),
   ORANGE("orange"),
   MAGENTA("magenta"),
   LIGHT_BLUE("light_blue"),
   YELLOW("yellow"),
   LIME("lime"),
   PINK("pink"),
   GRAY("gray"),
   LIGHT_GRAY("light_gray"),
   CYAN("cyan"),
   PURPLE("purple"),
   BLUE("blue"),
   BROWN("brown"),
   GREEN("green"),
   RED("red"),
   BLACK("black");

    private final String name;

    private ClothColor(String name) {
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
