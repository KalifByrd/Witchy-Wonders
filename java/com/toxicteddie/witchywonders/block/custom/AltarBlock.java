package com.toxicteddie.witchywonders.block.custom;

import org.jetbrains.annotations.Nullable;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.entity.AltarBlockEntity;
import com.toxicteddie.witchywonders.events.SoundEventHelper;
import com.toxicteddie.witchywonders.factions.Faction;
import com.toxicteddie.witchywonders.factions.FactionProvider;
import com.toxicteddie.witchywonders.factions.IFaction;
import com.toxicteddie.witchywonders.factions.IFaction.FactionType;
import com.toxicteddie.witchywonders.sound.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

public class AltarBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AltarPart> PART = EnumProperty.create("part", AltarPart.class);
    public static final EnumProperty<ClothColor> CLOTH_COLOR = EnumProperty.create("cloth_color", ClothColor.class);
    public static final EnumProperty<WoodType> WOOD_TYPE = EnumProperty.create("wood_type", WoodType.class);

    public AltarBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, AltarPart.CENTER)
            .setValue(CLOTH_COLOR, ClothColor.BLANK)
            .setValue(WOOD_TYPE, WoodType.DARK_OAK));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, CLOTH_COLOR, WOOD_TYPE);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        switch (pState.getValue(PART)) {
            case LEFT:
                return getLeftShape(direction);
            case RIGHT:
                return getRightShape(direction);
            case CENTER:
            default:
                return getCenterShape(direction);
        }
    }

    private VoxelShape getCenterShape(Direction direction) {
        VoxelShape shape = Shapes.box(0, 0.9375, 0.125, 1, 1, 0.875);
        return rotateShape(shape, direction);
    }

    private VoxelShape getLeftShape(Direction direction) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.5, 0.9375, 0.125, 1, 1, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.53125, 0.375, 0.15625, 0.65625, 0.9375, 0.28125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.53125, 0, 0.15625, 0.65625, 0.1875, 0.28125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.53125, 0, 0.71875, 0.65625, 0.1875, 0.84375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.1875, 0.1875, 0.625, 0.375, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.53125, 0.375, 0.71875, 0.65625, 0.9375, 0.84375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.1875, 0.75, 0.625, 0.375, 0.8125), BooleanOp.OR);
        return rotateShape(shape, direction);
    }

    private VoxelShape getRightShape(Direction direction) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.9375, 0.125, 0.5, 1, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.34375, 0.375, 0.15625, 0.46875, 0.9375, 0.28125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0.75, 0.4375, 0.375, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.34375, 0, 0.71875, 0.46875, 0.1875, 0.84375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.34375, 0.375, 0.71875, 0.46875, 0.9375, 0.84375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.34375, 0, 0.15625, 0.46875, 0.1875, 0.28125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0.1875, 0.4375, 0.375, 0.25), BooleanOp.OR);
        return rotateShape(shape, direction);
    }

    private VoxelShape rotateShape(VoxelShape shape, Direction direction) {
        switch (direction) {
            case SOUTH:
                return rotateShape(shape, 180);
            case WEST:
                return rotateShape(shape, 90);
            case EAST:
                return rotateShape(shape, 270);
            case NORTH:
            default:
                return shape;
        }
    }

    private VoxelShape rotateShape(VoxelShape shape, int angle) {
        List<AABB> boundingBoxes = shape.toAabbs();
        List<VoxelShape> rotatedShapes = new ArrayList<>();

        for (AABB box : boundingBoxes) {
            double minX = box.minX;
            double minY = box.minY;
            double minZ = box.minZ;
            double maxX = box.maxX;
            double maxY = box.maxY;
            double maxZ = box.maxZ;

            if (angle == 90) {
                rotatedShapes.add(Shapes.box(minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX));
            } else if (angle == 180) {
                rotatedShapes.add(Shapes.box(1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1 - minZ));
            } else if (angle == 270) {
                rotatedShapes.add(Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
            } else {
                rotatedShapes.add(Shapes.box(minX, minY, minZ, maxX, maxY, maxZ));
            }
        }

        VoxelShape result = Shapes.empty();
        for (VoxelShape rotatedShape : rotatedShapes) {
            result = Shapes.or(result, rotatedShape);
        }

        return result.optimize();
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AltarBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, WitchyWonders.ALTAR_BLOCK_ENTITY.get(), AltarBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos leftPos = pos.relative(direction.getCounterClockWise());
        BlockPos rightPos = pos.relative(direction.getClockWise());

        if (world.getBlockState(leftPos).getBlock() instanceof AltarBlock && 
            world.getBlockState(leftPos).getValue(PART) == AltarPart.CENTER) {
            return null; // Prevent placement next to another center part
        }

        if (world.getBlockState(rightPos).getBlock() instanceof AltarBlock && 
            world.getBlockState(rightPos).getValue(PART) == AltarPart.CENTER) {
            return null; // Prevent placement next to another center part
        }

        // Check if leftPos and rightPos are air blocks
        if (!world.getBlockState(leftPos).isAir() || !world.getBlockState(rightPos).isAir()) {
            return null; // Prevent placement if left or right positions are not air blocks
        }

        // Determine the wood type from the item stack
        WoodType woodType = getWoodTypeFromItem(context.getItemInHand());

        return this.defaultBlockState()
            .setValue(FACING, direction)
            .setValue(PART, AltarPart.CENTER)
            .setValue(CLOTH_COLOR, ClothColor.BLANK)
            .setValue(WOOD_TYPE, woodType);
    }

    private WoodType getWoodTypeFromItem(ItemStack stack) {
        if (stack.getItem() == WitchyWonders.ALTAR_ITEM_ACACIA.get()) {
            return WoodType.ACACIA;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_BAMBOO.get()) {
            return WoodType.BAMBOO;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_BIRCH.get()) {
            return WoodType.BIRCH;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_CHERRY.get()) {
            return WoodType.CHERRY;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_CRIMSON.get()) {
            return WoodType.CRIMSON;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_DARK_OAK.get()) {
            return WoodType.DARK_OAK;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_JUNGLE.get()) {
            return WoodType.JUNGLE;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_MANGROVE.get()) {
            return WoodType.MANGROVE;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_OAK.get()) {
            return WoodType.OAK;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_SPRUCE.get()) {
            return WoodType.SPRUCE;
        } else if (stack.getItem() == WitchyWonders.ALTAR_ITEM_WARPED.get()) {
            return WoodType.WARPED;
        } else {
            return WoodType.DARK_OAK;
        }
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        Direction direction = pState.getValue(FACING);
        BlockPos leftPos = pPos.relative(direction.getCounterClockWise());
        BlockPos rightPos = pPos.relative(direction.getClockWise());

        WoodType woodType = pState.getValue(WOOD_TYPE);

        if (pLevel.getBlockState(leftPos).isAir() && pLevel.getBlockState(rightPos).isAir()) {
            pLevel.setBlock(leftPos, this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(PART, AltarPart.LEFT)
                .setValue(WOOD_TYPE, woodType), 3);
            pLevel.setBlock(rightPos, this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(PART, AltarPart.RIGHT)
                .setValue(WOOD_TYPE, woodType), 3);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        Item item = heldItem.getItem();

        // Adjust positions and slot indices based on the direction the block is facing
        double relativeX = hit.getLocation().x - pos.getX();
        double relativeZ = hit.getLocation().z - pos.getZ();
        if (heldItem.isEmpty() && hand == InteractionHand.MAIN_HAND) {

            AltarPart part = state.getValue(PART);
    
            if (world.getBlockEntity(pos) instanceof AltarBlockEntity altarBlockEntity) {
                Direction pDirection = state.getValue(FACING);
                int index = getSlotFromHitPosition(pDirection, relativeX, relativeZ, part);
                // Handle shift-right-click with an empty hand to remove items or candles
                if (player.isShiftKeyDown()) {
                    Direction direction = state.getValue(FACING);
                    int slot = -1;

                    // Determine which slot is being interacted with
                    slot = getSlotFromHitPosition(direction, relativeX, relativeZ, part);
                    
                    

                    // Try to remove item from the determined slot
                    if (slot != -1 && altarBlockEntity.removeItem(slot, player)) {
                        return InteractionResult.SUCCESS;
                    }
                    // Determine which candle slot is being interacted with
                    slot = getCandleSlotFromHitPosition(direction, relativeX, relativeZ, part);
                    
                    

                    // Try to remove candle from the determined slot
                    if (slot != -1 && altarBlockEntity.removeCandle(slot, player)) {
                        return InteractionResult.SUCCESS;
                    }
                }
                if (index == -1) {
                    Direction direction = state.getValue(FACING);
                    // Handle candle interaction for the center part back
                    BlockState candleState = altarBlockEntity.getCandle(getCandleSlotFromHitPosition(direction, relativeX, relativeZ, part));
                    if (candleState != null) {
                        player.getCapability(FactionProvider.FACTION_CAP).ifPresent(faction -> {
                            IFaction.FactionType currentFaction = faction.getFaction();
                            // Handle lighting the candles
                            if (currentFaction == IFaction.FactionType.WITCH) {
                                BlockState newCandleState = candleState.cycle(CandleBlock.LIT);
                                altarBlockEntity.updateCandle(getCandleSlotFromHitPosition(direction, relativeX, relativeZ, part), newCandleState);
                            }
                        });
                        SoundEventHelper.playOneShotSound(Minecraft.getInstance(), ModSounds.MAGIC_SOUND.get());
                        world.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
                        return InteractionResult.SUCCESS;
                    }
                } else if (index >= 0 && index < 5) { // Ensure valid index range
                    // Handle item rotation
                    if (altarBlockEntity.rotateItem(index)) {
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        
        // Check if the held item is a BlockItem and if it is a carpet block
        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof CarpetBlock) {
            if (world.getBlockEntity(pos) instanceof AltarBlockEntity altarBlockEntity) {
                CarpetBlock carpetBlock = (CarpetBlock) blockItem.getBlock();
                ClothColor clothColor = getCarpetColor(carpetBlock);
                Direction direction = state.getValue(FACING);
                WoodType woodType = state.getValue(WOOD_TYPE);
                
                BlockPos centerPos = getCenterPos(state, pos);
                BlockPos leftPos = centerPos.relative(direction.getCounterClockWise());
                BlockPos rightPos = centerPos.relative(direction.getClockWise());

                // Destroy all parts
                world.destroyBlock(centerPos, false);
                world.destroyBlock(leftPos, false);
                world.destroyBlock(rightPos, false);
                if (!world.isClientSide && !player.isCreative() && state.getValue(CLOTH_COLOR) != ClothColor.BLANK) {
                    popResource(world, pos, new ItemStack(getCarpetBlock(state.getValue(CLOTH_COLOR))));
                }

                // Place new blocks with updated cloth color
                world.setBlock(centerPos, this.defaultBlockState()
                    .setValue(FACING, direction)
                    .setValue(PART, AltarPart.CENTER)
                    .setValue(CLOTH_COLOR, clothColor)
                    .setValue(WOOD_TYPE, woodType), 3);
                world.setBlock(leftPos, this.defaultBlockState()
                    .setValue(FACING, direction)
                    .setValue(PART, AltarPart.LEFT)
                    .setValue(CLOTH_COLOR, clothColor)
                    .setValue(WOOD_TYPE, woodType), 3);
                world.setBlock(rightPos, this.defaultBlockState()
                    .setValue(FACING, direction)
                    .setValue(PART, AltarPart.RIGHT)
                    .setValue(CLOTH_COLOR, clothColor)
                    .setValue(WOOD_TYPE, woodType), 3);
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }

                return InteractionResult.SUCCESS;
            }
        }

        // Check if the held item is a candle and if the cloth color is not blank
        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof CandleBlock && state.getValue(CLOTH_COLOR) != ClothColor.BLANK) {
            if (world.getBlockEntity(pos) instanceof AltarBlockEntity altarBlockEntity) {
                CandleBlock candleBlock = (CandleBlock) blockItem.getBlock();
                BlockState candleState = candleBlock.defaultBlockState().setValue(CandleBlock.CANDLES, 1).setValue(CandleBlock.LIT, false);

                // Determine the slot to place the candle based on hit position
                int slot = -1;
                Direction direction = state.getValue(FACING);
                AltarPart part = state.getValue(PART);
                slot = getCandleSlotFromHitPosition(direction, relativeX, relativeZ, part);
                

                if (slot != -1 && altarBlockEntity.setCandle(slot, candleState, player)) {
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (world.getBlockEntity(pos) instanceof AltarBlockEntity altarBlockEntity) {
            
            
            // Handle placing altar items if the cloth is not blank
            if (state.getValue(CLOTH_COLOR) != ClothColor.BLANK) {
                if (!heldItem.isEmpty()) {
                    Direction direction = state.getValue(FACING);
                    AltarPart part = state.getValue(PART);
    
                    // Determine placement position based on click location
                    int slot = -1;
                    slot = getSlotFromHitPosition(direction, relativeX, relativeZ, part);
    
                    if (slot != -1 && altarBlockEntity.setItem(slot, heldItem.copy(), player)) {
                        if (!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            // BlockState candleState = altarBlockEntity.getCandle(hit.getLocation(), pos);
            // if(candleState != null){
            //     player.getCapability(FactionProvider.FACTION_CAP).ifPresent(faction -> {
            //         IFaction.FactionType currentFaction = faction.getFaction();
            //         // Handle lighting the candles
            //         if (currentFaction == IFaction.FactionType.WITCH) {
            //             BlockState newCandleState = candleState.cycle(CandleBlock.LIT);
            //             altarBlockEntity.updateCandle(hit.getLocation(), pos, newCandleState);
            //         }
            //     });
            //     SoundEventHelper.playOneShotSound(Minecraft.getInstance(), ModSounds.MAGIC_SOUND.get());
            //     world.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.4F + 0.8F);
            //     return InteractionResult.SUCCESS;
            // }
            
                
            
        }
    
        return InteractionResult.PASS;
    }
    private int getCandleSlotFromHitPosition(Direction direction, double relativeX, double relativeZ, AltarPart part) {
        if (part != AltarPart.CENTER) {
            return -1;
        }

        switch (direction) {
            case NORTH:
                return getCandleIndexForHit(relativeX, relativeZ);
            case SOUTH:
                return getCandleIndexForHit(1 - relativeX, 1 - relativeZ);
            case WEST:
                return getCandleIndexForHit(1 - relativeZ, relativeX); // Flipped for west
            case EAST:
                return getCandleIndexForHit(relativeZ, 1 - relativeX); // Flipped for east
            default:
                return -1;
        }
    }

    private int getCandleIndexForHit(double relativeX, double relativeZ) {
        if (relativeZ > 0.5) {
            return relativeX < 0.5 ? 0 : 1; // Back left candle or back right candle
        }
        return -1;
    }
    private int getSlotFromHitPosition(Direction direction, double relativeX, double relativeZ, AltarPart part) {
        switch (direction) {
            case NORTH:
                return getPartIndexForHit(part, relativeX, relativeZ);
            case SOUTH:
                return getPartIndexForHit(part, 1 - relativeX, 1 - relativeZ);
            case WEST:
                return getPartIndexForHit(part, 1 - relativeZ, relativeX);
            case EAST:
                return getPartIndexForHit(part, relativeZ, 1- relativeX);
            default:
                return -1;
        }
    }
    
    private int getPartIndexForHit(AltarPart part, double relativeX, double relativeZ) {
        switch (part) {
            case CENTER:
                return relativeZ > 0.5 ? -1 : 0;
            case LEFT:
                return relativeZ > 0.5 ? 1 : 2;
            case RIGHT:
                return relativeZ > 0.5 ? 3 : 4;
            default:
                return -1;
        }
    }

    private void whichSlot(Direction direction, AltarPart part, int slot, BlockHitResult hit, BlockPos pos) {
        if(direction == Direction.NORTH){
            if (part == AltarPart.CENTER) {
                slot = 0; // Front middle item
            } else if (part == AltarPart.LEFT) {
                if (hit.getLocation().z - pos.getZ() > 0.5) {
                    slot = 1; // Back center left
                } else {
                    slot = 2; // Front center left
                }
            } else if (part == AltarPart.RIGHT) {
                if (hit.getLocation().z - pos.getZ() > 0.5) {
                    slot = 3; // Back center right
                } else {
                    slot = 4; // Front center right
                }
            }
        }
        else if(direction == Direction.SOUTH){

        }
    }


    private int getPartIndex(AltarPart part, BlockHitResult hit) {
        // Determine the index based on the part and hit location
        switch (part) {
            case CENTER:
                return hit.getLocation().z - hit.getBlockPos().getZ() > 0.5 ? -1 : 0;
            case LEFT:
                return hit.getLocation().z - hit.getBlockPos().getZ() > 0.5 ? 1 : 2;
            case RIGHT:
                return hit.getLocation().z - hit.getBlockPos().getZ() > 0.5 ? 3 : 4;
            default:
                return 0;
        }
    }

    private BlockPos getCenterPos(BlockState state, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        if (state.getValue(PART) == AltarPart.CENTER) {
            return pos;
        } else if (state.getValue(PART) == AltarPart.LEFT) {
            return pos.relative(direction.getClockWise());
        } else { // AltarPart.RIGHT
            return pos.relative(direction.getCounterClockWise());
        }
    }


    private ClothColor getCarpetColor(CarpetBlock carpetBlock) {
        if (carpetBlock == Blocks.WHITE_CARPET) return ClothColor.WHITE;
        if (carpetBlock == Blocks.ORANGE_CARPET) return ClothColor.ORANGE;
        if (carpetBlock == Blocks.MAGENTA_CARPET) return ClothColor.MAGENTA;
        if (carpetBlock == Blocks.LIGHT_BLUE_CARPET) return ClothColor.LIGHT_BLUE;
        if (carpetBlock == Blocks.YELLOW_CARPET) return ClothColor.YELLOW;
        if (carpetBlock == Blocks.LIME_CARPET) return ClothColor.LIME;
        if (carpetBlock == Blocks.PINK_CARPET) return ClothColor.PINK;
        if (carpetBlock == Blocks.GRAY_CARPET) return ClothColor.GRAY;
        if (carpetBlock == Blocks.LIGHT_GRAY_CARPET) return ClothColor.LIGHT_GRAY;
        if (carpetBlock == Blocks.CYAN_CARPET) return ClothColor.CYAN;
        if (carpetBlock == Blocks.PURPLE_CARPET) return ClothColor.PURPLE;
        if (carpetBlock == Blocks.BLUE_CARPET) return ClothColor.BLUE;
        if (carpetBlock == Blocks.BROWN_CARPET) return ClothColor.BROWN;
        if (carpetBlock == Blocks.GREEN_CARPET) return ClothColor.GREEN;
        if (carpetBlock == Blocks.RED_CARPET) return ClothColor.RED;
        if (carpetBlock == Blocks.BLACK_CARPET) return ClothColor.BLACK;
        return null;
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        Direction direction = pState.getValue(FACING);
        BlockPos centerPos;
        
        // Determine the center position based on which part was destroyed
        if (pState.getValue(PART) == AltarPart.CENTER) {
            centerPos = pPos;
        } else if (pState.getValue(PART) == AltarPart.LEFT) {
            centerPos = pPos.relative(direction.getClockWise());
        } else { // AltarPart.RIGHT
            centerPos = pPos.relative(direction.getCounterClockWise());
        }
        
        BlockPos leftPos = centerPos.relative(direction.getCounterClockWise());
        BlockPos rightPos = centerPos.relative(direction.getClockWise());
        
        // Destroy all parts
        removeItemsAndCandles(pLevel, centerPos);
        removeItemsAndCandles(pLevel, leftPos);
        removeItemsAndCandles(pLevel, rightPos);

        pLevel.destroyBlock(centerPos, false);
        pLevel.destroyBlock(leftPos, false);
        pLevel.destroyBlock(rightPos, false);

        // Drop the corresponding items
        if (!pLevel.isClientSide && !pPlayer.isCreative()) {
            popResource(pLevel, pPos, new ItemStack(getAltarItem(pState.getValue(WOOD_TYPE))));
            if (pState.getValue(CLOTH_COLOR) != ClothColor.BLANK) {
                popResource(pLevel, pPos, new ItemStack(getCarpetBlock(pState.getValue(CLOTH_COLOR))));
            }
        }

        
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }
    private void removeItemsAndCandles(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AltarBlockEntity) {
            AltarBlockEntity altarBlockEntity = (AltarBlockEntity) blockEntity;
            altarBlockEntity.removeAllItems();
            altarBlockEntity.removeAllCandles();
        }
    }

    private BlockItem getAltarItem(WoodType woodType) {
        switch (woodType) {
            case ACACIA:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_ACACIA.get();
            case BAMBOO:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_BAMBOO.get();
            case BIRCH:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_BIRCH.get();
            case CHERRY:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_CHERRY.get();
            case CRIMSON:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_CRIMSON.get();
            case DARK_OAK:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_DARK_OAK.get();
            case JUNGLE:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_JUNGLE.get();
            case MANGROVE:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_MANGROVE.get();
            case OAK:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_OAK.get();
            case SPRUCE:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_SPRUCE.get();
            case WARPED:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_WARPED.get();
            default:
                return (BlockItem) WitchyWonders.ALTAR_ITEM_DARK_OAK.get();
        }
    }
    
    private Item getCarpetBlock(ClothColor clothColor) {
        switch (clothColor) {
            case WHITE:
                return Items.WHITE_CARPET;
            case ORANGE:
                return Items.ORANGE_CARPET;
            case MAGENTA:
                return Items.MAGENTA_CARPET;
            case LIGHT_BLUE:
                return Items.LIGHT_BLUE_CARPET;
            case YELLOW:
                return Items.YELLOW_CARPET;
            case LIME:
                return Items.LIME_CARPET;
            case PINK:
                return Items.PINK_CARPET;
            case GRAY:
                return Items.GRAY_CARPET;
            case LIGHT_GRAY:
                return Items.LIGHT_GRAY_CARPET;
            case CYAN:
                return Items.CYAN_CARPET;
            case PURPLE:
                return Items.PURPLE_CARPET;
            case BLUE:
                return Items.BLUE_CARPET;
            case BROWN:
                return Items.BROWN_CARPET;
            case GREEN:
                return Items.GREEN_CARPET;
            case RED:
                return Items.RED_CARPET;
            case BLACK:
                return Items.BLACK_CARPET;
            default:
                return Items.WHITE_CARPET;
        }
    }
}
