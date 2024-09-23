package com.toxicteddie.witchywonders.brewing;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import com.toxicteddie.witchywonders.WitchyWonders;

@Mod.EventBusSubscriber(modid = WitchyWonders.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CustomBrewingRecipes {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        registerBrewingRecipes();
    }

    public static void registerBrewingRecipes() {
        // Add custom ingredient to allowed containers
        PotionBrewingReflection.addAllowedContainer(Ingredient.of(WitchyWonders.SUNFLOWER_SEED_POWDER_ITEM.get()));

        // Use reflection to add custom potion brewing recipe
        PotionBrewingReflection.addMix(Potions.WATER, WitchyWonders.SUNFLOWER_SEED_POWDER_ITEM.get(), WitchyWonders.BOTTLE_OF_OIL.get());
        //PotionBrewingReflection.addMix(Potions.EMPTY, WitchyWonders.SUNFLOWER_SEED_POWDER_ITEM.get(), WitchyWonders.BOTTLE_OF_OIL.get());
        
        // Example of adding a container mix if needed
        // PotionBrewingReflection.addContainerMix(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        // PotionBrewingReflection.addContainerMix(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        
    }
}
