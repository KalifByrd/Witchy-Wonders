package com.toxicteddie.witchywonders.factions.commands;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandRegistration {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        SetFactionCommand.register(event.getDispatcher());
    }
}