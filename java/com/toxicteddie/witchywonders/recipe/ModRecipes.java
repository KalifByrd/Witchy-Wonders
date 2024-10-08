package com.toxicteddie.witchywonders.recipe;

import com.toxicteddie.witchywonders.WitchyWonders;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, WitchyWonders.MODID);

    public static final RegistryObject<RecipeSerializer<MortarAndPestleRecipe>> ITEM_GROUNDING_SERIALIZER =
            SERIALIZERS.register("item_grounding", () -> MortarAndPestleRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<WitchsOvenRecipe>> WITCHS_OVEN_RECIPE_SERIALIZER =
            SERIALIZERS.register("oil_infusing", () -> WitchsOvenRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
