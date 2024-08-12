package net.jaju.subservermod.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.jaju.subservermod.entity.ModEntities;
import net.jaju.subservermod.entity.PlayerEntity;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PlayerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("playerEntity")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("skinName", StringArgumentType.string())
                                        .executes(context -> {
                                            CommandSourceStack source = context.getSource();
                                            String name = StringArgumentType.getString(context, "name");
                                            String skinName = StringArgumentType.getString(context, "skinName");

                                            ServerLevel world = source.getLevel();
                                            Vec3 position = source.getPosition();
                                            PlayerEntity entity = ModEntities.PLAYER_ENTITY.get().create(world);

                                            if (entity != null) {
                                                entity.setSkinPlayerName(skinName, name);
                                                entity.moveTo(position.x, position.y, position.z, source.getRotation().y, 0);
                                                world.addFreshEntity(entity);
                                                source.sendSuccess(() -> Component.literal("Spawned shop entity with player skin: " + skinName), true);
                                            } else {
                                                source.sendFailure(Component.literal("Failed to create shop entity"));
                                            }
                                            return 1;
                                        }))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    String name = StringArgumentType.getString(context, "name");

                                    ServerLevel world = source.getLevel();
                                    boolean found = false;

                                    for (Entity entity : world.getEntities().getAll()) {
                                        if (entity instanceof PlayerEntity && entity.getCustomName() != null && entity.getCustomName().getString().equals(name)) {
                                            entity.remove(Entity.RemovalReason.DISCARDED);
                                            found = true;
                                            source.sendSuccess(() -> Component.literal("Removed player entity with name: " + name), true);
                                            break;
                                        }
                                    }

                                    if (!found) {
                                        source.sendFailure(Component.literal("No player entity found with name: " + name));
                                    }
                                    return 1;
                                })))
                .then(Commands.literal("move")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("yaw", FloatArgumentType.floatArg(0.0F, 360.0F))
                                                .executes(context -> {
                                                    CommandSourceStack source = context.getSource();
                                                    String name = StringArgumentType.getString(context, "name");
                                                    BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                                    float yaw = FloatArgumentType.getFloat(context, "yaw");

                                                    ServerLevel world = source.getLevel();
                                                    boolean found = false;

                                                    for (Entity entity : world.getEntities().getAll()) {
                                                        if (entity instanceof PlayerEntity && entity.getCustomName() != null && entity.getCustomName().getString().equals(name)) {
                                                            ((PlayerEntity) entity).updatePositionAndRotation(pos, yaw);
                                                            found = true;
                                                            source.sendSuccess(() -> Component.literal("Moved player entity with name: " + name + " to position " + pos + " with yaw " + yaw), true);
                                                            break;
                                                        }
                                                    }

                                                    if (!found) {
                                                        source.sendFailure(Component.literal("No player entity found with name: " + name));
                                                    }
                                                    return 1;
                                                })))))
        );
    }
}
