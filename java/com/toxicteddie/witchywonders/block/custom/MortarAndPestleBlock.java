package com.toxicteddie.witchywonders.block.custom;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.entity.MortarAndPestleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MortarAndPestleBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = makeShape();

    public MortarAndPestleBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.375, 0.625, 0.0625, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.0625, 0.25, 0.625, 0.125, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.125, 0.25, 0.6875, 0.1875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.125, 0.6875, 0.6875, 0.1875, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.0625, 0.375, 0.375, 0.125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.125, 0.3125, 0.3125, 0.1875, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.0625, 0.375, 0.75, 0.125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.125, 0.375, 0.75, 0.1875, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.0625, 0.625, 0.6875, 0.125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.0625, 0.3125, 0.6875, 0.125, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.0625, 0.3125, 0.375, 0.125, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.125, 0.3125, 0.75, 0.1875, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.125, 0.625, 0.75, 0.1875, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.0625, 0.625, 0.375, 0.125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.65625, 0.1875, 0.3125, 0.78125, 0.3125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.1875, 0.65625, 0.6875, 0.3125, 0.78125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.1875, 0.21875, 0.6875, 0.3125, 0.34375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.21875, 0.1875, 0.3125, 0.34375, 0.3125, 0.6875), BooleanOp.OR);

        return shape;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MortarAndPestleBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, WitchyWonders.MORTAR_AND_PESTLE_BLOCK_ENTITY.get(), MortarAndPestleBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof MortarAndPestleBlockEntity) {
            MortarAndPestleBlockEntity mortarAndPestleBlockEntity = (MortarAndPestleBlockEntity) blockEntity;

            if (pPlayer.isShiftKeyDown()) {
                mortarAndPestleBlockEntity.removeItem(pPlayer);
            } else if (!stack.isEmpty()) {
                mortarAndPestleBlockEntity.addItem(stack, pPlayer);
            } else {
                mortarAndPestleBlockEntity.startAnimation(); // Start animation when crafting item
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MortarAndPestleBlockEntity) {
                ((MortarAndPestleBlockEntity) blockEntity).drops();
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}
