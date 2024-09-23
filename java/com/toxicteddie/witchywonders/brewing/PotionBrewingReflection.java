package com.toxicteddie.witchywonders.brewing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionBrewingReflection {
    private static Field allowedContainersField;
    private static Field containerMixesField;
    private static Method addMixMethod;

    static {
        try {
            allowedContainersField = net.minecraft.world.item.alchemy.PotionBrewing.class.getDeclaredField("ALLOWED_CONTAINERS");
            allowedContainersField.setAccessible(true);

            containerMixesField = net.minecraft.world.item.alchemy.PotionBrewing.class.getDeclaredField("CONTAINER_MIXES");
            containerMixesField.setAccessible(true);

            addMixMethod = net.minecraft.world.item.alchemy.PotionBrewing.class.getDeclaredMethod("addMix", Potion.class, Item.class, Potion.class);
            addMixMethod.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize PotionBrewingReflection", e);
        }
    }

    public static void addAllowedContainer(Ingredient ingredient) {
        try {
            List<Ingredient> allowedContainers = (List<Ingredient>) allowedContainersField.get(null);
            allowedContainers.add(ingredient);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add allowed container", e);
        }
    }

    public static void addMix(Potion input, Item ingredient, Potion output) {
        try {
            addMixMethod.invoke(null, input, ingredient, output);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke addMix", e);
        }
    }

    public static void addContainerMix(Item input, Item ingredient, Item output) {
        try {
            List<net.minecraft.world.item.alchemy.PotionBrewing.Mix<Item>> containerMixes = (List<net.minecraft.world.item.alchemy.PotionBrewing.Mix<Item>>) containerMixesField.get(null);
            containerMixes.add(new net.minecraft.world.item.alchemy.PotionBrewing.Mix<>(ForgeRegistries.ITEMS, input, Ingredient.of(ingredient), output));
        } catch (Exception e) {
            throw new RuntimeException("Failed to add container mix", e);
        }
    }
}
