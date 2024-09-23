package com.toxicteddie.witchywonders.block.entity;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.recipe.MortarAndPestleRecipe;
import com.toxicteddie.witchywonders.sound.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
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
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
    private int animationTime = 0;
    private int animationTicks = 0;
    private boolean isAnimating = false; // Flag for animation
    private boolean playSound = false; // Flag for sound
    private boolean wasAnimating = false; // Flag to track previous animation state
    
    

    public MortarAndPestleBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(WitchyWonders.MORTAR_AND_PESTLE_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        animationTime = tag.getInt("AnimationTime");
        animationTicks = tag.getInt("AnimationTicks");
        isAnimating = tag.getBoolean("isAnimating");
        playSound = tag.getBoolean("playSound");
        wasAnimating = tag.getBoolean("wasAnimating"); // Load wasAnimating flag
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
        tag.putInt("AnimationTime", animationTime);
        tag.putInt("AnimationTicks", animationTicks);
        tag.putBoolean("isAnimating", isAnimating);
        tag.putBoolean("playSound", playSound);
        tag.putBoolean("wasAnimating", wasAnimating); // Save wasAnimating flag
        super.saveAdditional(tag);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public ItemStack getRendererStack(int currentSlot) {
        return itemHandler.getStackInSlot(currentSlot);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
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
                
                // Play item pop sound
                level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition, GameEvent.Context.of(player, getBlockState()));
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
        if (!level.isClientSide && blockEntity.isAnimating) {
            blockEntity.animationTime++;
            blockEntity.animationTicks++;
            blockEntity.spawnCraftingParticles(level, pos); // Spawn particles during animation
            if (blockEntity.animationTime >= blockEntity.getMaxAnimationTime()) {
                blockEntity.isAnimating = false;
                blockEntity.animationTime = 0;
                blockEntity.craftItem();
                blockEntity.sendUpdateToClient();
                //WitchyWonders.LOGGER.info("Animation ended at position: " + pos);
            }
            blockEntity.sendUpdateToClient();
            
        }
        //WitchyWonders.LOGGER.info("Animation in progress at position: " + pos + " Time: " + blockEntity.animationTime);
        if (blockEntity.isAnimating && blockEntity.animationTime == 58) {
            blockEntity.playSound = true; // Set playSound flag when animation starts
            blockEntity.startSound(level, pos); // Start sound here
            
            blockEntity.sendUpdateToClient();
            
        }
        blockEntity.wasAnimating = blockEntity.isAnimating; // Update wasAnimating flag
        blockEntity.sendUpdateToClient();
    }

    private void sendUpdateToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private void spawnCraftingParticles(Level level, BlockPos pos) {
        // Logic to spawn particles around the block entity
        for (int i = 0; i < 20; i++) {
            level.addParticle(ParticleTypes.WHITE_ASH, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        }
    }

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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public void startAnimation() {
        if (!isAnimating && getCurrentRecipe().isPresent()) {
            isAnimating = true;
            animationTime = 0;
            playSound = true; // Reset the sound flag when animation start

            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3); // Ensure client-side update
            
            //WitchyWonders.LOGGER.info("Animation started at position: " + getBlockPos());
        }
    }
    private void startSound(Level level, BlockPos pos) {
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.MORTAR_AND_PESTLE_SOUND.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
    }
    

    public int getAnimationTime() {
        return animationTime;
    }

    public int getMaxAnimationTime() {
        return 100; // Duration of the animation
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public boolean shouldPlaySound() {
        return playSound;
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }
        
}
