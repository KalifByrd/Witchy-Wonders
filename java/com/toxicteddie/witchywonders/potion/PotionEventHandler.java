package com.toxicteddie.witchywonders.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.toxicteddie.witchywonders.WitchyWonders;

@Mod.EventBusSubscriber(modid = WitchyWonders.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PotionEventHandler {

    @SubscribeEvent
    public static void onPotionBrew(PotionBrewEvent.Pre event) {
        for (int i = 0; i < event.getLength(); i++) {
            ItemStack itemStack = event.getItem(i);

            if (itemStack.getItem() == Items.SPLASH_POTION || itemStack.getItem() == Items.LINGERING_POTION) {
                if (PotionUtils.getPotion(itemStack) == WitchyWonders.BOTTLE_OF_OIL.get() || 
                    PotionUtils.getPotion(itemStack) == WitchyWonders.OIL_OF_ANOINTING.get()) {
                    event.setItem(i, ItemStack.EMPTY);  // Clear the slot to prevent the potion from being brewed
                }
            }
        }
    }
}
