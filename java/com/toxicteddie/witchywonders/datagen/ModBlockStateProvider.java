package com.toxicteddie.witchywonders.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import com.toxicteddie.witchywonders.block.custom.HemlockCropBlock;

import java.util.function.Function;

import com.toxicteddie.witchywonders.WitchyWonders;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WitchyWonders.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //makeStrawberryCrop((CropBlock) ModBlocks.STRAWBERRY_CROP.get(), "strawberry_stage", "strawberry_stage");
        makeHemlockCrop(((CropBlock) WitchyWonders.HEMLOCK_CROP.get()), "corn_stage", "corn_stage");
    }


    // public void makeStrawberryCrop(CropBlock block, String modelName, String textureName) {
    //     Function<BlockState, ConfiguredModel[]> function = state -> strawberryStates(state, block, modelName, textureName);

    //     getVariantBuilder(block).forAllStates(function);
    // }

    // private ConfiguredModel[] strawberryStates(BlockState state, CropBlock block, String modelName, String textureName) {
    //     ConfiguredModel[] models = new ConfiguredModel[1];
    //     models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((StrawberryCropBlock) block).getAgeProperty()),
    //             new ResourceLocation(TutorialMod.MOD_ID, "block/" + textureName + state.getValue(((StrawberryCropBlock) block).getAgeProperty()))).renderType("cutout"));

    //     return models;
    // }

    public void makeHemlockCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> hemlockStates(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] hemlockStates(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((HemlockCropBlock) block).getAgeProperty()),
                new ResourceLocation(WitchyWonders.MODID, "block/" + textureName + state.getValue(((HemlockCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }
}