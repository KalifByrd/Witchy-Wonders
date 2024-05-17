package com.toxicteddie.witchywonders.block.entity;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.recipe.MortarAndPestleRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MortarAndPestleBlockEntity extends BlockEntity {
    private ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
    //private int craftTime;
    //private static final int CRAFT_TIME_TOTAL = 60; // 3 seconds (60 ticks)

    public MortarAndPestleBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(WitchyWonders.MORTAR_AND_PESTLE_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        //tag.putInt("CraftTime", craftTime);
        super.saveAdditional(tag);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public boolean addItem(ItemStack stack, Player player) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack slotStack = this.itemHandler.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                this.itemHandler.setStackInSlot(i, stack.split(1));
                return true;
            }
        }
        return false;
    }

    public void removeItem(Player player) {
        for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
            ItemStack slotStack = this.itemHandler.getStackInSlot(i);
            if (!slotStack.isEmpty()) {
                player.addItem(slotStack);
                this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                return;
            }
        }
    }

    private void removeItemsForRecipe(SimpleContainer inventory) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            this.itemHandler.extractItem(i, 1, false);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MortarAndPestleBlockEntity blockEntity) {
        // if (level.isClientSide) {
        //     return;
        // }

        // if (blockEntity.craftTime > 0) {
        //     blockEntity.craftTime--;
        //     blockEntity.spawnCraftingParticles(level, pos);
        //     if (blockEntity.craftTime == 0) {
        //         blockEntity.craftItem();
        //     }
        // } else {
        //     Optional<MortarAndPestleRecipe> recipe = blockEntity.getCurrentRecipe();
        //     if (recipe.isPresent()) {
        //         blockEntity.craftTime = CRAFT_TIME_TOTAL;
        //     }
        // }
    }

    // private void spawnCraftingParticles(Level level, BlockPos pos) {
    //     // Logic to spawn particles around the block entity
    //     level.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
    // }

    public void craftItem() {
        Optional<MortarAndPestleRecipe> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                inventory.setItem(i, this.itemHandler.getStackInSlot(i));
            }

            ItemStack output = recipe.get().assemble(inventory, level.registryAccess());
            removeItemsForRecipe(inventory);
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), output);
        }
    }

    private Optional<MortarAndPestleRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }
        return this.level.getRecipeManager().getRecipeFor(MortarAndPestleRecipe.Type.INSTANCE, inventory, level);
    }
}
