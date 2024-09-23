package com.toxicteddie.witchywonders.block.custom;

import org.jetbrains.annotations.Nullable;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.entity.MeditationBedBlockEntity;
import com.toxicteddie.witchywonders.block.entity.MortarAndPestleBlockEntity;
import com.toxicteddie.witchywonders.block.entity.WitchsOvenBlockEntity;
import com.toxicteddie.witchywonders.network.LayingDirectionSyncPacket;
import com.toxicteddie.witchywonders.network.MeditatePacket;
import com.toxicteddie.witchywonders.network.NetworkHandler;
import com.toxicteddie.witchywonders.network.PlayerPosePacket;
import com.toxicteddie.witchywonders.network.RotatePlayerPacket;
import com.toxicteddie.witchywonders.entity.custom.CustomPlayerEntityThin;
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
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

public class MeditationBedBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<WoodType> WOOD_TYPE = EnumProperty.create("wood_type", WoodType.class);
    public static final EnumProperty<MeditationBedPart> PART = EnumProperty.create("part", MeditationBedPart.class);
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    private static Boolean layDown = false;
    private static BlockPos bedBlockPos;
    private static BlockState bedState;
    private static CustomPlayerEntityThick customPlayerEntityThick;
    private static CustomPlayerEntityThin customPlayerEntityThin;

    private static final VoxelShape NORTH_FRONT_SHAPE = Shapes.or(
        Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.125, 1),
        Shapes.box(0, 0, 0, 1, 0.0625, 1)
    );
    
    private static final VoxelShape NORTH_BACK_SHAPE = Shapes.or(
        Shapes.box(0, 0, 0, 1, 0.0625, 1),
        Shapes.box(0.0625, 0.0625, 0, 0.9375, 0.125, 0.9375),
        Shapes.box(0.15625, 0.0625, 0.4375, 0.84375, 0.1875, 0.8125)
    );

    private static final VoxelShape EAST_FRONT_SHAPE = rotateShape(NORTH_FRONT_SHAPE, Direction.EAST);
    private static final VoxelShape EAST_BACK_SHAPE = rotateShape(NORTH_BACK_SHAPE, Direction.EAST);

    private static final VoxelShape SOUTH_FRONT_SHAPE = rotateShape(NORTH_FRONT_SHAPE, Direction.SOUTH);
    private static final VoxelShape SOUTH_BACK_SHAPE = rotateShape(NORTH_BACK_SHAPE, Direction.SOUTH);

    private static final VoxelShape WEST_FRONT_SHAPE = rotateShape(NORTH_FRONT_SHAPE, Direction.WEST);
    private static final VoxelShape WEST_BACK_SHAPE = rotateShape(NORTH_BACK_SHAPE, Direction.WEST);

    public MeditationBedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(OCCUPIED, Boolean.valueOf(false))
            .setValue(FACING, Direction.NORTH)
            .setValue(WOOD_TYPE, WoodType.CHERRY)
            .setValue(PART, MeditationBedPart.FRONT));
            
            
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WOOD_TYPE, PART, OCCUPIED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        switch (state.getValue(PART)) {
            case FRONT:
                return getFrontShape(direction);
            case BACK:
                return getBackShape(direction);
            default:
                return getFrontShape(direction);
        }
    }

    private VoxelShape getFrontShape(Direction direction) {
        switch (direction) {
            case NORTH:
                return NORTH_FRONT_SHAPE;
            case EAST:
                return EAST_FRONT_SHAPE;
            case SOUTH:
                return SOUTH_FRONT_SHAPE;
            case WEST:
                return WEST_FRONT_SHAPE;
            default:
                return NORTH_FRONT_SHAPE;
        }
    }

    private VoxelShape getBackShape(Direction direction) {
        switch (direction) {
            case NORTH:
                return NORTH_BACK_SHAPE;
            case EAST:
                return EAST_BACK_SHAPE;
            case SOUTH:
                return SOUTH_BACK_SHAPE;
            case WEST:
                return WEST_BACK_SHAPE;
            default:
                return NORTH_BACK_SHAPE;
        }
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (direction.get2DDataValue() + 2) % 4; // Rotate shape according to direction
        for (int i = 0; i < times; i++) {
            buffer[1] = Shapes.empty();
            buffer[0].forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
                buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2));
            });
            buffer[0] = buffer[1];
        }
        return buffer[0];
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
        Direction direction = context.getHorizontalDirection().getOpposite();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockPos behindPos = pos.relative(direction);

        if (!context.getLevel().getBlockState(behindPos).canBeReplaced(context)) {
            return null; // Prevent block placement if the second part cannot be placed
        }
        WoodType woodType = getWoodTypeFromItem(stack);

        return this.defaultBlockState()
            .setValue(OCCUPIED, false)
            .setValue(FACING, direction)
            .setValue(WOOD_TYPE, woodType)
            .setValue(PART, MeditationBedPart.FRONT);
    }

    private WoodType getWoodTypeFromItem(ItemStack stack) {
        if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_ACACIA.get()) {
            return WoodType.ACACIA;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_BAMBOO.get()) {
            return WoodType.BAMBOO;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_BIRCH.get()) {
            return WoodType.BIRCH;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_CHERRY.get()) {
            return WoodType.CHERRY;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_CRIMSON.get()) {
            return WoodType.CRIMSON;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_DARK_OAK.get()) {
            return WoodType.DARK_OAK;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_JUNGLE.get()) {
            return WoodType.JUNGLE;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_MANGROVE.get()) {
            return WoodType.MANGROVE;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_OAK.get()) {
            return WoodType.OAK;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_SPRUCE.get()) {
            return WoodType.SPRUCE;
        } else if (stack.getItem() == WitchyWonders.MEDITATION_BED_ITEM_WARPED.get()) {
            return WoodType.WARPED;
        } else {
            return WoodType.DARK_OAK;
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        if (state.getValue(PART) == MeditationBedPart.FRONT) {
            Direction direction = state.getValue(FACING);
            BlockPos offsetPos = pos.relative(direction.getOpposite());
            BlockState backState = this.defaultBlockState()
                .setValue(OCCUPIED, false)
                .setValue(FACING, direction)
                .setValue(WOOD_TYPE, state.getValue(WOOD_TYPE))
                .setValue(PART, MeditationBedPart.BACK);

            world.setBlock(offsetPos, backState, 3);
            
            
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            Direction direction = state.getValue(FACING);
            BlockPos adjacentPos = pos.relative(state.getValue(PART) == MeditationBedPart.FRONT ? direction.getOpposite() : direction);

            if (level.getBlockState(adjacentPos).getBlock() instanceof MeditationBedBlock) {
                level.destroyBlock(adjacentPos, false);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing == (state.getValue(PART) == MeditationBedPart.FRONT ? state.getValue(FACING).getOpposite() : state.getValue(FACING))) {
            if (!(facingState.getBlock() instanceof MeditationBedBlock)) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
        if (!level.isClientSide) {
        	player.getPersistentData().putBoolean("CustomHorizontalPose", true);
            layDown = !player.isCrouching();
            if(layDown && !state.getValue(OCCUPIED)){
                Direction direction = state.getValue(FACING);
                BlockPos backPartPos = pos.relative(direction);
                Vec3 bedPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.6875, pos.getZ() + 0.5);

                switch (direction) {
                    case NORTH:
                        bedPos = bedPos.add(0, 0, 0.3125);
                        break;
                    case SOUTH:
                        bedPos = bedPos.add(0, 0, -0.3125);
                        break;
                    case WEST:
                        bedPos = bedPos.add(0.3125, 0, 0);
                        break;
                    case EAST:
                        bedPos = bedPos.add(-0.3125, 0, 0);
                        break;
                }

                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PlayerPosePacket(pos, layDown, pos.getX(), pos.getY(), pos.getZ(), direction.toYRot(), direction));
                //NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new RotatePlayerPacket());
                level.setBlock(pos, state.setValue(OCCUPIED, true), 3);
                // Update the vanilla bed block's facing direction
                BlockPos bedPosBlock = pos.relative(direction);
             // Determine player model type and spawn the corresponding custom player entity
             // Determine player model type and spawn the corresponding custom player entity
                if (isPlayerUsingThinModel(player)) {
                	customPlayerEntityThin = new CustomPlayerEntityThin(ModEntities.CUSTOM_PLAYER_THIN.get(), level);
                    customPlayerEntityThin.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                    level.addFreshEntity(customPlayerEntityThin);
                    player.startRiding(customPlayerEntityThin, true);
                    //customPlayerEntityThin.startLayingAnimation(direction);
                } else {
                	
                	customPlayerEntityThick = new CustomPlayerEntityThick(ModEntities.CUSTOM_PLAYER_THICK.get(), level);
                	
                    //customPlayerEntityThick.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                	customPlayerEntityThick.moveTo(backPartPos.getX() + 0.5, backPartPos.getY() + 0.6875, backPartPos.getZ() + 0.5, player.getYRot(), player.getXRot());
                    level.addFreshEntity(customPlayerEntityThick);
                    WitchyWonders.LOGGER.info("our facing should be: " + direction);
                    WitchyWonders.LOGGER.info("the block is using entity: " + customPlayerEntityThick);
                    //customPlayerEntityThick.directionFacing = direction;
                    player.startRiding(customPlayerEntityThick, true);
                    //customPlayerEntityThick.updateLayingDirection(direction);
                    NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> customPlayerEntityThick), new LayingDirectionSyncPacket(customPlayerEntityThick.getId(), direction));
                    
                }

                player.setInvisible(true);
                
                bedBlockPos = pos;
                bedState = state;
                return InteractionResult.SUCCESS;
                
            }
        }
        return InteractionResult.CONSUME;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.level().isClientSide) {
            if(layDown) {
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new MeditatePacket(bedBlockPos, bedBlockPos.getX(), bedBlockPos.getY(), bedBlockPos.getZ(), bedState.getValue(FACING).toYRot()));
                if (player.isCrouching()) {
                    boolean getUp = !player.isCrouching();
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PlayerPosePacket(bedBlockPos, getUp, player.getX(), player.getY(), player.getZ(), Direction.NORTH.toYRot() + 180, Direction.NORTH));
                 // Kill custom player entity and revert to default model
                    if (customPlayerEntityThick != null) {
                        customPlayerEntityThick.remove(Entity.RemovalReason.DISCARDED);
                        customPlayerEntityThick = null;
                    }
                    if (customPlayerEntityThin != null) {
                        customPlayerEntityThin.remove(Entity.RemovalReason.DISCARDED);
                        customPlayerEntityThin = null;
                    }
                    player.setInvisible(false);
                    
                    // Reset the OCCUPIED state
                    Level level = player.level();
                    BlockState state = bedState;
                    level.setBlock(bedBlockPos, state.setValue(OCCUPIED, false), 3);
                    bedBlockPos = null;
                    bedState = null;
                    layDown = false;
                    
                }
            }
        }
    }
    protected InteractionResult onSleepInBed(Level level, BlockState blockState, BlockPos pos, Player player, InteractionHand hand)
	{
		Direction direction = blockState.getValue(FACING);
		var sleepPos = pos.relative(direction);
		level.setBlock(pos, blockState.setValue(OCCUPIED, true), 3);
        bedBlockPos = pos;
        bedState = blockState;

		player.startSleepInBed(sleepPos).ifLeft(result -> {
			if(result != null)
				player.displayClientMessage(result.getMessage(), true);
		});

		return InteractionResult.SUCCESS;
	}
    private boolean isPlayerUsingThinModel(Player player) {
        return "slim".equals(((ServerPlayer) player).getName());
    }
}