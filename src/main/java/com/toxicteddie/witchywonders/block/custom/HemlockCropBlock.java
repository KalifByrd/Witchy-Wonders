package com.toxicteddie.witchywonders.block.custom;

import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class HemlockCropBlock extends CropBlock {
    public static final int MAX_AGE = 5; 
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
    public static final IntegerProperty SECTION = IntegerProperty.create("section", 0, 1);

    public HemlockCropBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SECTION, 0).setValue(AGE, 0));
        //TODO Auto-generated constructor stub
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return WitchyWonders.HEMLOCK_SEEDS.get();
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE, SECTION);
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(SECTION) == 0) return super.canSurvive(state, world, pos);
        BlockState belowState = world.getBlockState(pos.below());
        return belowState.is(this) && belowState.getValue(SECTION) == 0;
    }


    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        if (world.isPositionEntityTicking(pos) && state.getValue(SECTION) == 0 && state.getValue(AGE) >= MAX_AGE && world.isEmptyBlock(pos.above())) {
            world.setBlockAndUpdate(pos.above(), this.defaultBlockState().setValue(SECTION, 1).setValue(AGE, 0));
        }
    }


    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);
        if (state.getValue(SECTION) == 0 && level.getBlockState(pos.above()).is(this)) {
            level.destroyBlock(pos.above(), true, player);
        }
        if (state.getValue(SECTION) == 1 && level.getBlockState(pos.below()).is(this)) {
            level.destroyBlock(pos.below(), true, player);
        }
    }

}

