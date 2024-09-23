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

public class MeditationBedBlockEntity extends BlockEntity {
    //public static boolean layDown;

    public MeditationBedBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(WitchyWonders.MEDITATION_BED_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MeditationBedBlockEntity blockEntity) {
        // if (!level.isClientSide) {
        //     Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
        //     boolean layDown = !player.isCrouching();
        //     Direction direction = state.getValue(MeditationBedBlock.FACING);

        //     //NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PlayerPosePacket(layDown, pos.getX(), pos.getY(), pos.getZ(), direction.toYRot() + 180));
        // }
    }
}
