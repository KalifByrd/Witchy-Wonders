package com.toxicteddie.witchywonders.block.custom;


import com.toxicteddie.witchywonders.WitchyWonders;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;

public class HemlockCropBlock extends CropBlock {
    public static final int MAX_AGE = 5;
    public static final int TRANSITION_START_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 8);
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    public HemlockCropBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_AGE[this.getAge(pState)];
    }
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isLoaded(pPos)) return;
        if (pLevel.getRawBrightness(pPos, 0) >= 9) {
            int currentAge = this.getAge(pState);
       

            if (currentAge < this.getMaxAge()) {
                float growthSpeed = getGrowthSpeed(this, pLevel, pPos);
                

                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(pLevel, pPos, pState, pRandom.nextInt((int)(25.0F / growthSpeed) + 1) == 0)) {
                    if ((currentAge + 1) >= TRANSITION_START_AGE) {
                        int topBlockAge = (currentAge + 1) + 3; // Compute the top block age based on nextAge
                    
                        // Set the block above to the computed top block age, and the current position to nextAge
                        pLevel.setBlock(pPos.above(1), this.getStateForAge(topBlockAge), 2);
                        pLevel.setBlock(pPos, this.getStateForAge(currentAge + 1), 2);
                    } else {
                        pLevel.setBlock(pPos, this.getStateForAge(currentAge + 1), 2);
                    }

                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(pLevel, pPos, pState);
                }
            }
        }
    }
    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable platable) {
        return super.mayPlaceOn(state, world, pos);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        // First, check if it can survive based on the superclass's criteria (usually checks if the block below is farmland).
        if (super.canSurvive(pState, pLevel, pPos)) {
            return true;
        }

        // Check if this block is supposed to be the top part of the crop.
        BlockState blockBelow = pLevel.getBlockState(pPos.below());
        if (blockBelow.is(this)) {
            int bottomBlockAge = blockBelow.getValue(AGE);
            // Allow survival if the block below is a part of this crop and has reached at least the transition start age.
            return bottomBlockAge >= TRANSITION_START_AGE && bottomBlockAge <= MAX_AGE;
        }

        return false;
    }
    // @Override
    // public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
    //     int age = getAge(state);

    //     if (!world.isClientSide) { // Check if not in creative mode
    //         world.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    //         if (player.isCreative()) return;
    //         Random rand = new Random();
            

    //         if (age == 1) {
    //             popResource(world, pos, new ItemStack(WitchyWonders.HEMLOCK_ROOT_ITEM.get(), 1));
    //         } else if (age == 5) {
    //             if (rand.nextBoolean()) { // 50% chance to drop an extra seed
    //                 popResource(world, pos, new ItemStack(WitchyWonders.HEMLOCK_SEEDS.get(), 1));
    //             }
    //             popResource(world, pos, new ItemStack(WitchyWonders.HEMLOCK_FLOWER_ITEM.get(), 1));
    //         } else if (age == 8) {
    //             popResource(world, pos, new ItemStack(WitchyWonders.HEMLOCK_SEEDS.get(), 1 + rand.nextInt(2))); // 1 to 2 seeds
    //             popResource(world, pos, new ItemStack(WitchyWonders.HEMLOCK_FLOWER_ITEM.get(), 1));
    //         }
    //     }
    //     world.removeBlock(pos, false);
    // }
    
    @Override
    public void growCrops(Level pLevel, BlockPos pPos, BlockState pState) {
        int nextAge = this.getAge(pState) + this.getBonemealAgeIncrease(pLevel);
        int maxAge = this.getMaxAge();

        if(nextAge > maxAge) {
            nextAge = maxAge;
        }
        
        if (nextAge >= TRANSITION_START_AGE) {
            int topBlockAge = nextAge + 3; // Compute the top block age based on nextAge
        
            // Set the block above to the computed top block age, and the current position to nextAge
            pLevel.setBlock(pPos.above(1), this.getStateForAge(topBlockAge), 2);
            pLevel.setBlock(pPos, this.getStateForAge(nextAge), 2);
        } else {
            // If not reaching the transition start age, just log and update the current block
            pLevel.setBlock(pPos, this.getStateForAge(nextAge), 2);
        }

        
    }
    
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return WitchyWonders.HEMLOCK_SEEDS.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder){
        pBuilder.add(AGE);
    } 
    

}

