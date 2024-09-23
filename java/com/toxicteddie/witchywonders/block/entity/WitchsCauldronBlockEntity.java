package com.toxicteddie.witchywonders.block.entity;

import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WitchsCauldronBlockEntity extends BlockEntity {

    public WitchsCauldronBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(WitchyWonders.WITCHS_CAULDRON_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WitchsCauldronBlockEntity blockEntity) {

    }

    
}
