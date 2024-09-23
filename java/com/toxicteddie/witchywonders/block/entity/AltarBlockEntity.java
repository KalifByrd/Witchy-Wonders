package com.toxicteddie.witchywonders.block.entity;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.custom.AltarBlock;
import com.toxicteddie.witchywonders.block.custom.AltarPart;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AltarBlockEntity extends BlockEntity {
    private ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
    private BlockState[] candles = new BlockState[2]; // Assuming you have two candles
    private final int[] angles = new int[5];

    public AltarBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(WitchyWonders.ALTAR_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        for (int i = 0; i < candles.length; i++) {
            candles[i] = Blocks.AIR.defaultBlockState();
        }
    }
    public boolean rotateItem(int index) {
        if (index >= 0 && index < angles.length) {
            int currentAngle = getAngle(index);
            int newAngle = (currentAngle + 1) % 8; // Rotate by 45 degrees each time (360 / 45 = 8)
            setAngle(index, newAngle);
            setChanged();
            return true;
        }
        return false;
    }

    public int getAngle(int index) {
        return angles[index];
    }

    public void setAngle(int index, int angle) {
        angles[index] = angle;
    }

    public ItemStack[] getItems() {
        ItemStack[] items = new ItemStack[itemHandler.getSlots()];
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            items[i] = itemHandler.getStackInSlot(i);
        }
        return items;
    }

    public boolean setItem(int index, ItemStack itemStack, Player player) {
        if (index >= 0 && index < itemHandler.getSlots()) {
            ItemStack previousItemStack = itemHandler.getStackInSlot(index);
            itemStack.setCount(1);
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), previousItemStack);
            itemHandler.setStackInSlot(index, itemStack);
            setChanged();
            return true;
        }
        return false;
    }

    public boolean setCandle(int index, BlockState candleState, Player player) {
        if (index >= 0 && index < candles.length) {
            ItemStack candleStack = new ItemStack(candles[index].getBlock().asItem());
            candleStack.setCount(1);
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), candleStack);
            candles[index] = candleState;
            setChanged();
            return true;
        }
        return false;
    }

    public boolean removeItem(int index, Player player) {
        if (index >= 0 && index < itemHandler.getSlots() && !itemHandler.getStackInSlot(index).isEmpty()) {
            if (!player.addItem(itemHandler.getStackInSlot(index))) {
                ItemStack itemStack = itemHandler.getStackInSlot(index);
                itemStack.setCount(1);
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), itemStack);
            }
            itemHandler.setStackInSlot(index, ItemStack.EMPTY);
            setChanged();
            return true;
        }
        return false;
    }
    public void removeAllItems() {
        for(int i = 0; i<itemHandler.getSlots(); i++){
            ItemStack itemStack = itemHandler.getStackInSlot(i);
            if(!itemStack.isEmpty()){
                itemStack.setCount(1);
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), itemStack);
                itemHandler.setStackInSlot(i, ItemStack.EMPTY); // Clear the slot
            }
        }
        setChanged(); // Ensure changes are saved
    }
    public void removeAllCandles() {
        for(int i = 0; i<candles.length; i++){
            ItemStack candleStack = new ItemStack(candles[i].getBlock().asItem());
            if(!candleStack.isEmpty()){
                candleStack.setCount(1);
                //popResource(pLevel, pPos, candleStack);
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), candleStack);
                candles[i] = Blocks.AIR.defaultBlockState(); // Clear the candle
            }
        }
        setChanged(); // Ensure changes are saved
    }
    
    public boolean removeCandle(int index, Player player) {
        if (index >= 0 && index < candles.length && !candles[index].isAir()) {
            ItemStack candleStack = new ItemStack(candles[index].getBlock().asItem());
            candleStack.setCount(1);
            if (!player.addItem(candleStack)) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), new ItemStack(candles[index].getBlock().asItem()));
            }
            candles[index] = Blocks.AIR.defaultBlockState();
            setChanged();
            return true;
        }
        return false;
    }

    public BlockState getCandle(int index) {
        if (index == 0) {
            return candles[0]; // back left candle
        } else if (index == 1) {
            return candles[1]; // back right candle
        }
        return null;
    }

    public BlockState[] getCandles() {
        return candles;
    }

    public void updateCandle(int index, BlockState newCandleState) {
        candles[index] = newCandleState;
        setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        
        ListTag candleList = tag.getList("Candles", Tag.TAG_COMPOUND);
        for (int i = 0; i < candleList.size(); i++) {
            CompoundTag candleTag = candleList.getCompound(i);
            int index = candleTag.getInt("Index");
            if (index >= 0 && index < candles.length) {
                candles[index] = BlockState.CODEC.parse(NbtOps.INSTANCE, candleTag.get("BlockState")).resultOrPartial(WitchyWonders.LOGGER::error).orElse(Blocks.AIR.defaultBlockState());
            }
        }

        ListTag angleList = tag.getList("Angles", Tag.TAG_INT);
        for (int i = 0; i < angleList.size(); i++) {
            angles[i] = angleList.getInt(i);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        
        ListTag candleList = new ListTag();
        for (int i = 0; i < candles.length; i++) {
            if (!candles[i].isAir()) {
                CompoundTag candleTag = new CompoundTag();
                candleTag.putInt("Index", i);
                BlockState.CODEC.encodeStart(NbtOps.INSTANCE, candles[i]).resultOrPartial(WitchyWonders.LOGGER::error).ifPresent(encoded -> candleTag.put("BlockState", encoded));
                candleList.add(candleTag);
            }
        }
        tag.put("Candles", candleList);

        ListTag angleList = new ListTag();
        for (int i = 0; i < angles.length; i++) {
            angleList.add(IntTag.valueOf(angles[i]));
        }
        tag.put("Angles", angleList);

        super.saveAdditional(tag);
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AltarBlockEntity blockEntity) {
        // Add any necessary ticking behavior here
    }

    private BlockPos getCenterPos(BlockState state, BlockPos pos) {
        Direction direction = state.getValue(AltarBlock.FACING);
        if (state.getValue(AltarBlock.PART) == AltarPart.CENTER) {
            return pos;
        } else if (state.getValue(AltarBlock.PART) == AltarPart.LEFT) {
            return pos.relative(direction.getClockWise());
        } else { // AltarPart.RIGHT
            return pos.relative(direction.getCounterClockWise());
        }
    }
}
