package com.toxicteddie.witchywonders.block.custom;

import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MandrakeCropBlock extends CropBlock {
    public static final int MAX_AGE = 5;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 5);
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
        Block.box(6.0D, 0.0D, 6.0D, 10.0D, 2.0D, 10.0D), // Stage 0
        Block.box(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D), // Stage 1
        Block.box(4.0D, 0.0D, 4.0D, 12.0D, 6.0D, 12.0D), // Stage 2
        Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D), // Stage 3
        Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D), // Stage 4
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D)}; // Stage 5

    public MandrakeCropBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_AGE[this.getAge(pState)];
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return WitchyWonders.MANDRAKE_SEEDS.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }


}


