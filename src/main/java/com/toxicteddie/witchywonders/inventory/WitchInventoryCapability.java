package com.toxicteddie.witchywonders.inventory;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WitchInventoryCapability implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<WitchInventory> WITCH_INVENTORY_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    

    private LazyOptional<WitchInventory> instance;

    public WitchInventoryCapability(Player player) {
        instance = LazyOptional.of(() -> new WitchInventory(player));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == WITCH_INVENTORY_CAP ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        instance.ifPresent(inventory -> {
            CompoundTag inventoryData = new CompoundTag();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    stack.save(itemTag);
                    inventoryData.put(String.valueOf(i), itemTag);
                }
            }
            tag.put("Inventory", inventoryData);
        });
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (instance.isPresent()) {
            WitchInventory inventory = instance.orElseThrow(IllegalStateException::new);
            CompoundTag inventoryData = nbt.getCompound("Inventory");
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                CompoundTag itemTag = inventoryData.getCompound(String.valueOf(i));
                if (!itemTag.isEmpty()) {
                    inventory.setItem(i, ItemStack.of(itemTag));
                }
            }
        }
    }
    
    

}
