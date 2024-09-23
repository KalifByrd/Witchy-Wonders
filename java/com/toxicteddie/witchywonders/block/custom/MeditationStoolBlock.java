package com.toxicteddie.witchywonders.block.custom;

import org.jetbrains.annotations.Nullable;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.entity.MeditationBedBlockEntity;
import com.toxicteddie.witchywonders.network.LayingDirectionSyncPacket;
import com.toxicteddie.witchywonders.network.MeditatePacket;
import com.toxicteddie.witchywonders.network.NetworkHandler;
import com.toxicteddie.witchywonders.network.PlayerPosePacket;
import com.toxicteddie.witchywonders.network.RotatePlayerPacket;
import com.toxicteddie.witchywonders.entity.custom.CustomPlayerEntityThin;
import com.toxicteddie.witchywonders.entity.custom.SeatEntity;
import com.toxicteddie.witchywonders.entity.custom.CustomPlayerEntityThick;
import com.toxicteddie.witchywonders.entity.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MeditationStoolBlock extends BaseEntityBlock {
    private static final VoxelShape NORTH_SHAPE = makeShape(Direction.NORTH);
    private static final VoxelShape SOUTH_SHAPE = makeShape(Direction.SOUTH);
    private static final VoxelShape WEST_SHAPE = makeShape(Direction.WEST);
    private static final VoxelShape EAST_SHAPE = makeShape(Direction.EAST);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WoodType> WOOD_TYPE = EnumProperty.create("wood_type", WoodType.class);

    public MeditationStoolBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(WOOD_TYPE, WoodType.CHERRY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WOOD_TYPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case EAST:
                return EAST_SHAPE;
            default:
                return NORTH_SHAPE;
        }
    }

    private static VoxelShape makeShape(Direction direction) {
        VoxelShape shape = Shapes.empty();
        switch (direction) {
            case SOUTH:
                shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 0.25, 0.9375, 0.25, 0.75), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.6875, 0.125, 0.1875, 0.75), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.875, 0, 0.6875, 0.9375, 0.1875, 0.75), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.875, 0, 0.25, 0.9375, 0.1875, 0.3125), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.25, 0.125, 0.1875, 0.3125), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.125, 0.0625, 0.3125, 0.875, 0.1875, 0.6875), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.09375, 0.21875, 0.28125, 0.90625, 0.28125, 0.71875), BooleanOp.OR);
                break;
            case WEST:
                shape = Shapes.join(shape, Shapes.box(0.25, 0.1875, 0.0625, 0.75, 0.25, 0.9375), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.0625, 0.75, 0.1875, 0.125), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.875, 0.75, 0.1875, 0.9375), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.875, 0.3125, 0.1875, 0.9375), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.0625, 0.3125, 0.1875, 0.125), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.3125, 0.0625, 0.125, 0.6875, 0.1875, 0.875), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.28125, 0.21875, 0.09375, 0.71875, 0.28125, 0.90625), BooleanOp.OR);
                break;
            case EAST:
                shape = Shapes.join(shape, Shapes.box(0.25, 0.1875, 0.0625, 0.75, 0.25, 0.9375), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.0625, 0.75, 0.1875, 0.125), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.875, 0.75, 0.1875, 0.9375), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.875, 0.3125, 0.1875, 0.9375), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.0625, 0.3125, 0.1875, 0.125), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.3125, 0.0625, 0.125, 0.6875, 0.1875, 0.875), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.28125, 0.21875, 0.09375, 0.71875, 0.28125, 0.90625), BooleanOp.OR);
                break;
            case NORTH:
            default:
                shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 0.25, 0.9375, 0.25, 0.75), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.6875, 0.125, 0.1875, 0.75), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.875, 0, 0.6875, 0.9375, 0.1875, 0.75), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.875, 0, 0.25, 0.9375, 0.1875, 0.3125), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.25, 0.125, 0.1875, 0.3125), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.125, 0.0625, 0.3125, 0.875, 0.1875, 0.6875), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.09375, 0.21875, 0.28125, 0.90625, 0.28125, 0.71875), BooleanOp.OR);
                break;
        }
        return shape;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MeditationBedBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(WOOD_TYPE, getWoodTypeFromItem(context.getItemInHand()));
    }

    private WoodType getWoodTypeFromItem(ItemStack stack) {
        if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_ACACIA.get()) {
            return WoodType.ACACIA;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_BAMBOO.get()) {
            return WoodType.BAMBOO;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_BIRCH.get()) {
            return WoodType.BIRCH;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_CHERRY.get()) {
            return WoodType.CHERRY;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_CRIMSON.get()) {
            return WoodType.CRIMSON;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_DARK_OAK.get()) {
            return WoodType.DARK_OAK;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_JUNGLE.get()) {
            return WoodType.JUNGLE;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_MANGROVE.get()) {
            return WoodType.MANGROVE;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_OAK.get()) {
            return WoodType.OAK;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_SPRUCE.get()) {
            return WoodType.SPRUCE;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_STOOL_ITEM_WARPED.get()) {
            return WoodType.WARPED;
        } else {
            return WoodType.DARK_OAK;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockPos seatPos = pos.above();
            SeatEntity seat = new SeatEntity(level, seatPos);
            seat.setPos(seat.getX(), seat.getY() - 1.45, seat.getZ()); // Adjust the Y-coordinate to lower the seat
            level.addFreshEntity(seat);
            player.startRiding(seat);
        }
        return InteractionResult.SUCCESS;
    }



    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.level().isClientSide) {
            // Handle player tick event
        }
    }
}
