package com.toxicteddie.witchywonders.inventory;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = "witchywonders", bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilitySetup {

    // Define the Capability statically using CapabilityToken for type safety.
    public static final Capability<WitchInventory> WITCH_INVENTORY_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Optional: Any additional setup related to the capability could go here
            // Most capability setups don't need additional initialization besides getting the capability instance
        });
    }
}
