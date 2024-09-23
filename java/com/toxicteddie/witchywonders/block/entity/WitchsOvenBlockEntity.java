package com.toxicteddie.witchywonders.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.custom.WitchsOvenBlock;
import com.toxicteddie.witchywonders.recipe.WitchsOvenRecipe;
import com.toxicteddie.witchywonders.screen.WitchsOvenMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.slf4j.Logger;

public class WitchsOvenBlockEntity extends BlockEntity implements MenuProvider {
    private static final Logger LOGGER = WitchyWonders.LOGGER;
    private final ItemStackHandler itemHandler = new ItemStackHandler(7);

    private static final int INGREDIENT_SLOT_ONE = 0;
    private static final int INGREDIENT_SLOT_TWO = 1;
    private static final int INGREDIENT_SLOT_THREE = 2;

    private static final int OIL_SLOT = 3;
    
    private static final int FUEL_SLOT = 4;

    private static final int OUTPUT_SLOT_ONE = 5;
    private static final int OUTPUT_SLOT_TWO = 6;

    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;

    int litTime;
    int litDuration;
    private int progress = 0;
    private int maxProgress = 78;

    public WitchsOvenBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(WitchyWonders.WITCHS_OVEN_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                switch (pIndex) {
                    case 0: return WitchsOvenBlockEntity.this.litTime;
                    case 1: return WitchsOvenBlockEntity.this.litDuration;
                    case 2: return WitchsOvenBlockEntity.this.progress;
                    case 3: return WitchsOvenBlockEntity.this.maxProgress;
                    default: return 0;
                }
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: WitchsOvenBlockEntity.this.litTime = pValue; break;
                    case 1: WitchsOvenBlockEntity.this.litDuration = pValue; break;
                    case 2: WitchsOvenBlockEntity.this.progress = pValue; break;
                    case 3: WitchsOvenBlockEntity.this.maxProgress = pValue; break;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        LOGGER.info("Witch's Oven loaded at position {}", this.worldPosition);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        LOGGER.info("Capabilities invalidated for Witch's Oven at position {}", this.worldPosition);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
        LOGGER.info("Dropped items from Witch's Oven at position {}", this.worldPosition);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WitchsOvenBlockEntity blockEntity) {
        boolean wasLit = blockEntity.isLit();
        boolean stateChanged = false;
    
        if (blockEntity.isLit()) {
            --blockEntity.litTime;
            
            // Emit particles if lit
            if (level instanceof ServerLevel) {
                RandomSource random = level.random;
                Direction facing = state.getValue(WitchsOvenBlock.FACING);

                // Flame particles at the front bottom of the block
                double flameX = pos.getX() + 0.5;
                double flameY = pos.getY() + 0.36; // Lower the flame particles
                double flameZ = pos.getZ() + 0.26; // Move to the front of the block
                
                
                // Campfire smoke particles at the back right corner
                double smokeX = pos.getX() + 0.2;
                double smokeY = pos.getY() + 2.45; // Adjust to chimney height
                double smokeZ = pos.getZ() + 0.75;

                switch (facing) {
                    case NORTH:
                        flameZ = pos.getZ() + 0.26;
                        smokeX = pos.getX() + 0.2;
                        smokeZ = pos.getZ() + 0.75;
                        break;
                    case SOUTH:
                        flameZ = pos.getZ() + 0.74;
                        smokeX = pos.getX() + 0.8;
                        smokeZ = pos.getZ() + 0.25;
                        break;
                    case WEST:
                        flameX = pos.getX() + 0.26;
                        flameZ = pos.getZ() + 0.5;
                        smokeX = pos.getX() + 0.75;
                        smokeZ = pos.getZ() + 0.75;
                        break;
                    case EAST:
                        flameX = pos.getX() + 0.74;
                        flameZ = pos.getZ() + 0.5;
                        smokeX = pos.getX() + 0.25;
                        smokeZ = pos.getZ() + 0.25;
                        break;
                    default:
                        break;
                }

                // Spawn fewer particles and make them smaller
                ((ServerLevel) level).sendParticles(ParticleTypes.FLAME, flameX, flameY, flameZ, 1, 0.2, 0.1, 0.2, 0.0);
                ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, flameX, flameY, flameZ, 1, 0.2, 0.1, 0.2, 0.0);
                // Emit smoke particles that rise straight up
                // Emit smoke particles that rise straight up with no horizontal movement
                //if (pos.getY() >= 10.0) {
                    if(random.nextInt(10) == 0) {
                        ((ServerLevel) level).sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, smokeX, smokeY, smokeZ, 1, 0.0, 0.69, 0.0, 0.0001);
                    }
                //}
            }
        }
    
        ItemStack fuelStack = blockEntity.itemHandler.getStackInSlot(FUEL_SLOT);
        ItemStack oilStack = blockEntity.itemHandler.getStackInSlot(OIL_SLOT);
        boolean hasOil = !oilStack.isEmpty();
    
        if (blockEntity.isLit() || (!fuelStack.isEmpty() && hasOil && blockEntity.hasValidRecipe())) {
            if (!blockEntity.isLit() && blockEntity.canBurn()) {
                blockEntity.litTime = blockEntity.getBurnDuration(fuelStack);
                blockEntity.litDuration = blockEntity.litTime;
                if (blockEntity.isLit()) {
                    stateChanged = true;
                    if (fuelStack.hasCraftingRemainingItem()) {
                        blockEntity.itemHandler.setStackInSlot(FUEL_SLOT, fuelStack.getCraftingRemainingItem());
                    } else {
                        fuelStack.shrink(1);
                        if (fuelStack.isEmpty()) {
                            blockEntity.itemHandler.setStackInSlot(FUEL_SLOT, fuelStack.getCraftingRemainingItem());
                        }
                    }
                }
            }
    
            if (blockEntity.isLit() && blockEntity.canBurn()) {
                ++blockEntity.progress;
                if (blockEntity.progress >= blockEntity.maxProgress) {
                    blockEntity.progress = 0; // Reset progress when recipe is complete
                    blockEntity.craftItem();
                    blockEntity.generateByproduct();
                    stateChanged = true;
                }
            } else {
                blockEntity.progress = 0;
            }
        } else if (!blockEntity.isLit() && blockEntity.progress > 0) {
            blockEntity.progress = Math.max(0, blockEntity.progress - 2);
        }
    
        if (wasLit != blockEntity.isLit()) {
            stateChanged = true;
            state = state.setValue(WitchsOvenBlock.LIT, blockEntity.isLit());
            level.setBlock(pos, state, 3);
        }
        
    
        if (stateChanged) {
            setChanged(level, pos, state);
        }
    }
    

    private boolean canBurn() {
        boolean canBurn = hasValidRecipe() && !this.itemHandler.getStackInSlot(OIL_SLOT).isEmpty() && isFuelAvailable();
        LOGGER.debug("Evaluated canBurn to {}", canBurn);
        return canBurn;
    }

    private boolean hasValidRecipe() {
        ItemStack ingredientOne = this.itemHandler.getStackInSlot(INGREDIENT_SLOT_ONE);
        ItemStack ingredientTwo = this.itemHandler.getStackInSlot(INGREDIENT_SLOT_TWO);
        ItemStack ingredientThree = this.itemHandler.getStackInSlot(INGREDIENT_SLOT_THREE);
        SimpleContainer inventory = new SimpleContainer(ingredientOne, ingredientTwo, ingredientThree);
    
        LOGGER.debug("Checking for valid recipe with inventory: [Slot 1: {}, Slot 2: {}, Slot 3: {}]", ingredientOne, ingredientTwo, ingredientThree);
    
        Optional<WitchsOvenRecipe> recipe = this.level.getRecipeManager().getRecipeFor(WitchsOvenRecipe.Type.INSTANCE, inventory, this.level);
        
        boolean hasRecipe = recipe.isPresent();
        if (hasRecipe) {
            LOGGER.debug("Found valid recipe: {}", recipe.get().getId());
        } else {
            LOGGER.debug("No valid recipe found for the given ingredients.");
        }
        return hasRecipe;
    }
    

    private boolean isFuelAvailable() {
        ItemStack fuelStack = this.itemHandler.getStackInSlot(FUEL_SLOT);
        boolean fuelAvailable = !fuelStack.isEmpty() && net.minecraftforge.common.ForgeHooks.getBurnTime(fuelStack, null) > 0;
        LOGGER.debug("Evaluated isFuelAvailable to {}", fuelAvailable);
        return fuelAvailable;
    }

    private void craftItem() {
        ItemStack ingredientOne = this.itemHandler.getStackInSlot(INGREDIENT_SLOT_ONE);
        ItemStack ingredientTwo = this.itemHandler.getStackInSlot(INGREDIENT_SLOT_TWO);
        ItemStack ingredientThree = this.itemHandler.getStackInSlot(INGREDIENT_SLOT_THREE);

        // Only include non-empty slots in the SimpleContainer
        List<ItemStack> ingredients = new ArrayList<>();
        if (!ingredientOne.isEmpty()) ingredients.add(ingredientOne);
        if (!ingredientTwo.isEmpty()) ingredients.add(ingredientTwo);
        if (!ingredientThree.isEmpty()) ingredients.add(ingredientThree);

        SimpleContainer inventory = new SimpleContainer(ingredients.toArray(new ItemStack[0]));

        Optional<WitchsOvenRecipe> recipe = this.level.getRecipeManager().getRecipeFor(WitchsOvenRecipe.Type.INSTANCE, inventory, this.level);
    
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().assemble(inventory, level.registryAccess());
            ItemStack outputSlot = this.itemHandler.getStackInSlot(OUTPUT_SLOT_ONE);
            if (outputSlot.isEmpty()) {
                this.itemHandler.setStackInSlot(OUTPUT_SLOT_ONE, result.copy());
            } else if (ItemStack.isSameItem(outputSlot, result)) {
                outputSlot.grow(result.getCount());
            }
    
            // Consume ingredients
            for (int i = INGREDIENT_SLOT_ONE; i <= INGREDIENT_SLOT_THREE; i++) {
                ItemStack slot = this.itemHandler.getStackInSlot(i);
                if (!slot.isEmpty()) {
                    slot.shrink(1);
                }
            }
    
            // Consume oil
            ItemStack oilStack = this.itemHandler.getStackInSlot(OIL_SLOT);
            if (!oilStack.isEmpty()) {
                oilStack.shrink(1);
                if (oilStack.isEmpty()) {
                    this.itemHandler.setStackInSlot(OIL_SLOT, ItemStack.EMPTY);
                }
            }
        }
    }
    
    private void generateByproduct() {
        // Logic to generate a byproduct based on output slot one and update output slot two
        ItemStack outputOne = this.itemHandler.getStackInSlot(OUTPUT_SLOT_ONE);
        if (!outputOne.isEmpty()) {
            ItemStack byproduct = getRandomByproduct(outputOne);
            ItemStack outputTwo = this.itemHandler.getStackInSlot(OUTPUT_SLOT_TWO);
            if (outputTwo.isEmpty()) {
                this.itemHandler.setStackInSlot(OUTPUT_SLOT_TWO, byproduct.copy());
            } else if (ItemStack.isSameItem(outputTwo, byproduct)) {
                outputTwo.grow(byproduct.getCount());
            }
        }
    }
    
    private ItemStack getRandomByproduct(ItemStack outputOne) {
        // Logic to determine a random byproduct based on the item in output slot one
        // This is a placeholder implementation, replace with your actual byproduct logic
        // For example purposes, let's assume it always returns a specific item
        return new ItemStack(Items.SLIME_BALL); // Example item
    }
    
    protected int getBurnDuration(ItemStack stack) {
        // Logic to get the burn duration for the given fuel item
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, null);
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new WitchsOvenMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.witchywonders.witchs_oven");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("BurnTime", this.litTime);
        tag.putInt("LitDuration", this.litDuration);
        tag.putInt("witchs_oven.progress", this.progress);
        tag.putInt("witchs_oven.max_progress", this.maxProgress);
    }



    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        this.litTime = tag.getInt("BurnTime");
        this.litDuration = tag.getInt("LitDuration");
        this.progress = tag.getInt("witchs_oven.progress");
        this.maxProgress = tag.getInt("witchs_oven.max_progress");
    }


}
