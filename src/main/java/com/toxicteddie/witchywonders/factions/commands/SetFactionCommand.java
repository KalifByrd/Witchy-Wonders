package com.toxicteddie.witchywonders.factions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.toxicteddie.witchywonders.factions.FactionProvider;
import com.toxicteddie.witchywonders.factions.IFaction;
import com.toxicteddie.witchywonders.network.FactionUpdatePacket;
import com.toxicteddie.witchywonders.network.NetworkHandler;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.network.chat.Component;
import java.util.concurrent.CompletableFuture;

public class SetFactionCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("setfaction")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                .suggests(SetFactionCommand::suggestPlayerNames) // Attach suggestPlayerNames here
                    .then(Commands.literal("WITCH")
                        .executes(ctx -> setFaction(ctx, IFaction.FactionType.WITCH)))
                    .then(Commands.literal("HUMAN")
                        .executes(ctx -> setFaction(ctx, IFaction.FactionType.HUMAN))))
        );
        dispatcher.register(
            Commands.literal("getfaction")
                .requires(cs -> cs.hasPermission(0))
                .executes(SetFactionCommand::getFaction)
        );
    }

    private static CompletableFuture<Suggestions> suggestPlayerNames(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (String name : context.getSource().getServer().getPlayerNames()) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }

    private static int setFaction(CommandContext<CommandSourceStack> context, IFaction.FactionType newType) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        player.getCapability(FactionProvider.FACTION_CAP).ifPresent(faction -> {
            IFaction.FactionType currentFaction = faction.getFaction();
            if (currentFaction == newType) {
                context.getSource().sendSuccess(() -> Component.literal("The player is already a " + newType.name().toLowerCase() + "."), false);
            } else {
                faction.setFaction(newType);
                context.getSource().sendSuccess(() -> Component.literal("Changed player from " + currentFaction + " to " + newType.name() + "."), false);
                FactionUpdatePacket packet = new FactionUpdatePacket(newType.name());
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
            }
        });

        return 1; // Command was executed successfully
    }
    private static int getFaction(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.getCapability(FactionProvider.FACTION_CAP).ifPresent(faction -> {
            IFaction.FactionType currentFaction = faction.getFaction();
            context.getSource().sendSuccess(() -> Component.literal("Your faction is: " + currentFaction), false);
        });

        return 1; // Command was executed successfully
    }
}
