package com.toxicteddie.witchywonders.block.entity;

import com.toxicteddie.witchywonders.WitchyWonders;
import com.toxicteddie.witchywonders.block.custom.MeditationBedBlock;
import com.toxicteddie.witchywonders.network.NetworkHandler;
import com.toxicteddie.witchywonders.network.PlayerPosePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class MeditationStoolBlockEntity extends BlockEntity {
    //public static boolean layDown;

    public MeditationStoolBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(WitchyWonders.MEDITATION_STOOL_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MeditationBedBlockEntity blockEntity) {
 
    }
}
