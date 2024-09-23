package com.toxicteddie.witchywonders;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;

@Mod.EventBusSubscriber(modid = "witchywonders", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootTableHandler {

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(new ResourceLocation("minecraft", "blocks/sunflower"))) {
            LootPool pool = LootPool.lootPool()
                    .name("sunflower_seeds_pool")
                    .add(LootItem.lootTableItem(WitchyWonders.SUNFLOWER_SEEDS_ITEM.get())
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))))
                    .when(LootItemRandomChanceCondition.randomChance(0.05f))
                    .build();
            
            event.getTable().addPool(pool);
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(LootTableHandler.class);
    }
}
