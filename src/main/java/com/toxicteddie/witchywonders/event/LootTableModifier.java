package com.toxicteddie.witchywonders.event;

import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "witchywonders")
public class LootTableModifier {

    private static final ResourceLocation GRASS_LOOT_TABLE = new ResourceLocation("minecraft", "blocks/grass");

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(GRASS_LOOT_TABLE)) {
            LootPool.Builder builder = LootPool.lootPool()
                .name("witchywonders_seeds")
                .setRolls(UniformGenerator.between(0.2f, 0.5f));  // Lowered the minimum and maximum rolls to make seeds rarer

            // Adding Hemlock Seeds with a 5% chance of dropping
            builder.add(LootItem.lootTableItem(WitchyWonders.HEMLOCK_SEEDS.get())
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                .when(LootItemRandomChanceCondition.randomChance(0.05f)));

            // Repeat for other seeds with similar rare drop chances
            builder.add(LootItem.lootTableItem(WitchyWonders.BELLADONNA_SEEDS.get())
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                .when(LootItemRandomChanceCondition.randomChance(0.05f)));

            builder.add(LootItem.lootTableItem(WitchyWonders.WOLFSBANE_SEEDS.get())
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                .when(LootItemRandomChanceCondition.randomChance(0.05f)));

            builder.add(LootItem.lootTableItem(WitchyWonders.MANDRAKE_SEEDS.get())
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                .when(LootItemRandomChanceCondition.randomChance(0.05f)));

            builder.add(LootItem.lootTableItem(WitchyWonders.VERVAIN_SEEDS.get())
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                .when(LootItemRandomChanceCondition.randomChance(0.05f)));

            builder.add(LootItem.lootTableItem(WitchyWonders.SAGE_SEEDS.get())
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                .when(LootItemRandomChanceCondition.randomChance(0.05f)));

            event.getTable().addPool(builder.build());
        }
    }
}

