package com.toxicteddie.witchywonders.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;

public class WitchInventory extends Inventory implements IWitchInventory {
    private ItemStack[] additionalSlots = new ItemStack[4]; // Add four custom slots

    public WitchInventory(Player player) {
        super(player);
        // Initialize the additional slots to empty
        for (int i = 0; i < additionalSlots.length; i++) {
            additionalSlots[i] = ItemStack.EMPTY;
        }
    }

    // Example method to access an additional slot
    public ItemStack getAdditionalSlot(int index) {
        if (index >= 0 && index < additionalSlots.length) {
            return additionalSlots[index];
        }
        return ItemStack.EMPTY;
    }

    public void setAdditionalSlot(int index, ItemStack stack) {
        if (index >= 0 && index < additionalSlots.length) {
            additionalSlots[index] = stack;
        }
    }

    // Override to include additional slots in inventory operations
    @Override
    public int getContainerSize() {
        return super.getContainerSize() + additionalSlots.length;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index >= super.getContainerSize()) {
            return getAdditionalSlot(index - super.getContainerSize());
        }
        return super.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= super.getContainerSize()) {
            setAdditionalSlot(index - super.getContainerSize(), stack);
        } else {
            super.setItem(index, stack);
        }
    }

    @Override
    public boolean add(ItemStack stack) {
        // Override without adding the ability to place items in additional slots
        return super.add(stack);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (index >= super.getContainerSize()) {
            ItemStack itemStack = getAdditionalSlot(index - super.getContainerSize());
            if (!itemStack.isEmpty()) {
                if (itemStack.getCount() <= count) {
                    setAdditionalSlot(index - super.getContainerSize(), ItemStack.EMPTY);
                    return itemStack;
                } else {
                    return itemStack.split(count);
                }
            }
        }
        return super.removeItem(index, count);
    }

    // Override to prevent removing items directly from additional slots
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (index >= super.getContainerSize()) {
            // Prevent any item removal from additional slots
            return ItemStack.EMPTY;
        }
        return super.removeItemNoUpdate(index);
    }

    
}

