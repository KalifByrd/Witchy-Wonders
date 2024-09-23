package com.toxicteddie.witchywonders.block.custom;

import org.jetbrains.annotations.Nullable;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.entity.WitchsCauldronBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitchsCauldronBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = makeShape();

    public WitchsCauldronBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.25, 0.375, 0.0625, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0625, 0.1875, 0.8125, 0.125, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.125, 0.875, 0.4375, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0, 0.25, 0.75, 0.0625, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0, 0.625, 0.75, 0.0625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.625, 0.375, 0.0625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.8125, 0.875, 0.4375, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.1875, 0.1875, 0.4375, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.125, 0.1875, 0.875, 0.4375, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.4375, 0.1875, 0.8125, 0.5, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.4375, 0.75, 0.8125, 0.5, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.4375, 0.25, 0.25, 0.5, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0.4375, 0.25, 0.8125, 0.5, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.5, 0.125, 0.875, 0.625, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.5, 0.8125, 0.875, 0.625, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.5, 0.1875, 0.1875, 0.625, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.5, 0.1875, 0.875, 0.625, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.09375, 0.25, 0.375, 0.15625, 0.375, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.09375, 0.25, 0.5625, 0.15625, 0.375, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.09375, 0.1875, 0.4375, 0.15625, 0.25, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.84375, 0.1875, 0.4375, 0.90625, 0.25, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.84375, 0.25, 0.375, 0.90625, 0.375, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.84375, 0.25, 0.5625, 0.90625, 0.375, 0.625), BooleanOp.OR);

        return shape;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WitchsCauldronBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, WitchyWonders.WITCHS_CAULDRON_BLOCK_ENTITY.get(), WitchsCauldronBlockEntity::tick);
    }
    
}
