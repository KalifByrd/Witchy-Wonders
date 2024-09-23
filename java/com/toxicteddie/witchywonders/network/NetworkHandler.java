package com.toxicteddie.witchywonders.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation("witchywonders", "main_channel"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, FactionUpdatePacket.class,
                                 FactionUpdatePacket::encode,
                                 FactionUpdatePacket::decode,
                                 ClientPacketHandler::handle);
        INSTANCE.registerMessage(id++, FireProjectilePacket.class,
                                 FireProjectilePacket::encode,
                                 FireProjectilePacket::decode,
                                 FireProjectilePacket::handle);

        INSTANCE.registerMessage(id++, SetEntityOnFirePacket.class,
                                 SetEntityOnFirePacket::encode,
                                 SetEntityOnFirePacket::decode,
                                 SetEntityOnFirePacket::handle);

        INSTANCE.registerMessage(id++, SetBlockOnFirePacket.class,
                                 SetBlockOnFirePacket::encode,
                                 SetBlockOnFirePacket::decode,
                                 SetBlockOnFirePacket::handle);
        INSTANCE.registerMessage(id++, PlayerPosePacket.class, PlayerPosePacket::toBytes, PlayerPosePacket::new, PlayerPosePacket::handle);
        INSTANCE.registerMessage(id++, MeditatePacket.class, MeditatePacket::toBytes, MeditatePacket::new, MeditatePacket::handle);
        INSTANCE.registerMessage(id++, RotatePlayerPacket.class, RotatePlayerPacket::toBytes, RotatePlayerPacket::new, RotatePlayerPacket::handle);
        INSTANCE.registerMessage(id++, LayingDirectionSyncPacket.class, LayingDirectionSyncPacket::encode, LayingDirectionSyncPacket::decode, LayingDirectionSyncPacket::handle);
        
    }
}
