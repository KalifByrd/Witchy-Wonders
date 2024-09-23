package com.toxicteddie.witchywonders.inventory;

import com.toxicteddie.witchywonders.factions.FactionProvider;
import com.toxicteddie.witchywonders.factions.IFaction;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WitchInventoryCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation CAPABILITY_ID = new ResourceLocation("witchywonders", "extended_witch_inventory");

    private WitchInventory extendedInventory;
    private final LazyOptional<WitchInventory> instance;

    public WitchInventoryCapabilityProvider(Player player) {
        this.extendedInventory = new WitchInventory(player);
        this.instance = LazyOptional.of(() -> extendedInventory);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == WitchInventoryCapability.WITCH_INVENTORY_CAP) {
            return instance.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();  // Serialize the custom inventory here if necessary
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // Deserialize the custom inventory here if necessary
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            Player player = (Player) event.getObject();
            if (shouldBeWitch(player)) { // Implement this method based on your game logic
                WitchInventoryCapabilityProvider provider = new WitchInventoryCapabilityProvider(player);
                event.addCapability(CAPABILITY_ID, provider);
            }
        }
    }

    private static boolean shouldBeWitch(Player player) {
        LazyOptional<IFaction> factionCap = player.getCapability(FactionProvider.FACTION_CAP);
        return factionCap.map(faction -> faction.getFaction() == IFaction.FactionType.WITCH).orElse(false);
    }
}
