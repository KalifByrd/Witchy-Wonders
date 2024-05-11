package com.toxicteddie.witchywonders.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetBlockOnFirePacket {
    private final BlockPos blockPos;
    private final Direction direction;

    public SetBlockOnFirePacket(BlockPos blockPos, Direction direction) {
        this.blockPos = blockPos;
        this.direction = direction;
    }

    public static void encode(SetBlockOnFirePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.blockPos);
        buf.writeEnum(packet.direction);
    }

    public static SetBlockOnFirePacket decode(FriendlyByteBuf buf) {
        BlockPos blockPos = buf.readBlockPos();
        Direction direction = buf.readEnum(Direction.class);
        return new SetBlockOnFirePacket(blockPos, direction);
    }

    public static void handle(SetBlockOnFirePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level level = player.level();
                BlockPos pos = packet.blockPos; // Use the position directly, not relative
                BlockState state = level.getBlockState(pos);
    
                // Check if the block is a campfire and if it's not already lit
                if (state.is(BlockTags.CAMPFIRES) && !state.getValue(BlockStateProperties.LIT)) {
                    // Set the campfire to lit
                    level.setBlock(pos, state.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
                    level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
                }
                else {
                    BlockPos targetPos = pos.relative(packet.direction); // Only use relative for non-campfire blocks
                    BlockState targetState = level.getBlockState(targetPos);
                    level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
                    // Check if the target position is air and if the base block is flammable
                    if (targetState.isAir() && (state.isFlammable(level, pos, packet.direction) || canForceFireOnBlock(state))) {
                        level.setBlock(targetPos, Blocks.FIRE.defaultBlockState(), 11);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
    
    private static boolean canForceFireOnBlock(BlockState blockState) {
        // Add any specific blocks you want to force to be flammable
        return blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.OBSIDIAN);
    }
}
