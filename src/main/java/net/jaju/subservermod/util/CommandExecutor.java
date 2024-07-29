package net.jaju.subservermod.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
public class CommandExecutor {
    public static void executeCommand(ServerPlayer player, String command) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            CommandSourceStack commandSourceStack = player.createCommandSourceStack()
                    .withSuppressedOutput();
            server.getCommands().performPrefixedCommand(commandSourceStack, command);
        }
    }
}