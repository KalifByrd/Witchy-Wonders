package com.toxicteddie.witchywonders.inventory;

import net.minecraft.world.item.ItemStack;

public interface IWitchInventory {
    void setAdditionalSlot(int index, ItemStack stack);
    ItemStack getAdditionalSlot(int index);
}
