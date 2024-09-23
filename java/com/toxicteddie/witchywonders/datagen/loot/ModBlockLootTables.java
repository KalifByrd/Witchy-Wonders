package com.toxicteddie.witchywonders.datagen.loot;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.custom.HemlockCropBlock;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        // LootItemCondition.Builder lootitemcondition$builder = LootItemBlockStatePropertyCondition
        //         .hasBlockStateProperties(ModBlocks.STRAWBERRY_CROP.get())
        //         .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StrawberryCropBlock.AGE, 5));

        // this.add(ModBlocks.STRAWBERRY_CROP.get(), createCropDrops(ModBlocks.STRAWBERRY_CROP.get(), ModItems.STRAWBERRY.get(),
        //         ModItems.STRAWBERRY_SEEDS.get(), lootitemcondition$builder));


        // Create loot conditions for each growth stage
        WitchyWonders.LOGGER.info("Generating loot tables for Hemlock Crop");
        LootItemCondition.Builder age1Condition = LootItemBlockStatePropertyCondition
            .hasBlockStateProperties(WitchyWonders.HEMLOCK_CROP.get())
            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HemlockCropBlock.AGE, 1));
        WitchyWonders.LOGGER.debug("Loot conditions set for age 1");
        LootItemCondition.Builder age5Condition = LootItemBlockStatePropertyCondition
            .hasBlockStateProperties(WitchyWonders.HEMLOCK_CROP.get())
            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HemlockCropBlock.AGE, 5));
        WitchyWonders.LOGGER.debug("Loot conditions set for age 5");
        // Define the loot table for Hemlock crop
        this.add(WitchyWonders.HEMLOCK_CROP.get(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .when(age1Condition)
                .add(LootItem.lootTableItem(WitchyWonders.HEMLOCK_SEEDS.get())
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                .add(LootItem.lootTableItem(WitchyWonders.HEMLOCK_ROOT_ITEM.get())
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))))
            .withPool(LootPool.lootPool()
                .when(age5Condition)
                .add(LootItem.lootTableItem(WitchyWonders.HEMLOCK_SEEDS.get())
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                .add(LootItem.lootTableItem(WitchyWonders.HEMLOCK_FLOWER_ITEM.get())
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
        ));
        WitchyWonders.LOGGER.info("Loot tables generated successfully for Hemlock Crop");
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return WitchyWonders.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}