package net.jaju.subservermod.commands.encyclopedia;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.jaju.subservermod.manager.EncyclopediaManager;
import net.jaju.subservermod.manager.LandManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EncyclopediaCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("encyclopedia")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("top")
                        .then(Commands.argument("num", IntegerArgumentType.integer(1))
                            .executes(context -> {
                                listTopDiscoverers(context.getSource(), IntegerArgumentType.getInteger(context, "num"));
                                return 1;
                            }))));
    }

    private static void listTopDiscoverers(CommandSourceStack source, int num) {
        Map<UUID, Integer> topDiscoverers = EncyclopediaManager.getInstance().getTopDiscoverers(num);

        if (topDiscoverers.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No data available"), false);
            return;
        }

        source.sendSuccess(() -> Component.literal("Top "+ num +" Discoverers:"), false);
        int rank = 1;
        for (Map.Entry<UUID, Integer> entry : topDiscoverers.entrySet()) {
            UUID playerUUID = entry.getKey();
            int discoveryCount = entry.getValue();
            int finalRank = rank;
            String playerName = LandManager.getPlayerName(playerUUID);
            source.sendSuccess(() -> Component.literal(finalRank + ". " + playerName + ": " + discoveryCount + " items discovered"), false);
            rank++;
        }
    }
}
