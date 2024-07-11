// ClassCommand.java
package net.jaju.subservermod.subclass;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ClassCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("class")
                .requires(source -> source.hasPermission(2)) // OP 권한 체크
                .then(Commands.literal("add")
                        .then(Commands.argument("playerName", StringArgumentType.string())
                                .then(Commands.argument("className", StringArgumentType.string())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                                .executes(context -> {
                                                    String playerName = StringArgumentType.getString(context, "playerName");
                                                    String className = StringArgumentType.getString(context, "className");
                                                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                                                    ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
                                                    int level = IntegerArgumentType.getInteger(context, "level");

                                                    BaseClass playerClass;
                                                    switch (className.toLowerCase()) {
                                                        case "farmer":
                                                            playerClass = new Farmer(level, playerName);
                                                            playerClass.performSkill("give", player);
                                                            break;
                                                        case "miner":
                                                            playerClass = new Miner(level, playerName);
                                                            playerClass.performSkill("give", player);
                                                            break;
                                                        case "alchemist":
                                                            playerClass = new Alchemist(level, playerName);
                                                            playerClass.performSkill("give", player);
                                                            break;
                                                        default:
                                                            context.getSource().sendFailure(Component.literal("Unknown class: " + className));
                                                            return 0;
                                                    }

                                                    ClassManagement.addClass(playerName, playerClass);
                                                    context.getSource().sendSuccess(() -> Component.literal("Added class " + className + " to " + playerName), true);
                                                    return 1;
                                                })))))
                .then(Commands.literal("set")
                        .then(Commands.argument("playerName", StringArgumentType.string())
                                .then(Commands.argument("className", StringArgumentType.string())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                                .executes(context -> {
                                                    String playerName = StringArgumentType.getString(context, "playerName");
                                                    String className = StringArgumentType.getString(context, "className");
                                                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                                                    ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
                                                    int level = IntegerArgumentType.getInteger(context, "level");

                                                    BaseClass playerClass;
                                                    switch (className.toLowerCase()) {
                                                        case "farmer":
                                                            playerClass = new Farmer(level, playerName);
                                                            playerClass.performSkill("give", player);
                                                            break;
                                                        case "miner":
                                                            playerClass = new Miner(level, playerName);
                                                            playerClass.performSkill("give", player);
                                                            break;
                                                        case "alchemist":
                                                            playerClass = new Alchemist(level, playerName);
                                                            playerClass.performSkill("give", player);
                                                            break;

                                                        default:
                                                            context.getSource().sendFailure(Component.literal("Unknown class: " + className));
                                                            return 0;
                                                    }

                                                    ClassManagement.setClass(playerName, playerClass);
                                                    context.getSource().sendSuccess(() -> Component.literal("Set class " + className + " for " + playerName), true);
                                                    return 1;
                                                }))))));
    }
}
