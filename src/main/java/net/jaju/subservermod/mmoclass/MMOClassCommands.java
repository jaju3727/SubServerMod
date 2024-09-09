package net.jaju.subservermod.mmoclass;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.coinsystem.CoinData;
import net.jaju.subservermod.coinsystem.CoinHud;
import net.jaju.subservermod.coinsystem.network.CoinDataUpdatePacket;
import net.jaju.subservermod.integrated_menu.network.TemporaryOpRequestPacket;
import net.jaju.subservermod.shopsystem.network.UpdateInventoryPacket;
import net.jaju.subservermod.util.CommandExecutor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

import java.util.Collection;
import java.util.UUID;

@Mod.EventBusSubscriber
public class MMOClassCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("mmoclass")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("get")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> {
                                    Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");

                                    for (ServerPlayer targetPlayer : targets) {
                                        UUID playerUUID = targetPlayer.getUUID();
                                        MMOClassManagement classManager = MMOClassManagement.getInstance();
                                        String playerClass = classManager.getPlayerClass(playerUUID);
                                        int playerLevel = classManager.getPlayerLevel(playerUUID);

                                        context.getSource().sendSuccess(() -> Component.literal(
                                                "Player " + targetPlayer.getName().getString() + " is a " + playerClass + " at level " + playerLevel), false);
                                    }
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("setclass")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("class", StringArgumentType.string())
                                        .executes(context -> {
                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                            String newClass = StringArgumentType.getString(context, "class");

                                            for (ServerPlayer targetPlayer : targets) {
                                                UUID playerUUID = targetPlayer.getUUID();
                                                MMOClassManagement classManager = MMOClassManagement.getInstance();
                                                classManager.setPlayerClass(playerUUID, newClass);

                                                ModNetworking.INSTANCE.sendTo(
                                                        new ClassDataSyncPacket(playerUUID, newClass, classManager.getPlayerLevel(playerUUID)),
                                                        targetPlayer.connection.connection,
                                                        NetworkDirection.PLAY_TO_CLIENT
                                                );

                                                context.getSource().sendSuccess(() -> Component.literal(
                                                        "Set " + targetPlayer.getName().getString() + "'s class to " + newClass), false);
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("setlevel")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 5)) // Set level between 1 and 5
                                        .executes(context -> {
                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                            int newLevel = IntegerArgumentType.getInteger(context, "level");

                                            for (ServerPlayer targetPlayer : targets) {
                                                UUID playerUUID = targetPlayer.getUUID();
                                                MMOClassManagement classManager = MMOClassManagement.getInstance();
                                                classManager.setPlayerLevel(playerUUID, newLevel);

                                                ModNetworking.INSTANCE.sendTo(
                                                        new ClassDataSyncPacket(playerUUID, classManager.getPlayerClass(playerUUID), newLevel),
                                                        targetPlayer.connection.connection,
                                                        NetworkDirection.PLAY_TO_CLIENT
                                                );

                                                context.getSource().sendSuccess(() -> Component.literal(
                                                        "Set " + targetPlayer.getName().getString() + "'s level to " + newLevel), false);
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("update")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> {
                                    MMOClassManagement classManager = MMOClassManagement.getInstance();

                                    ServerPlayer player = context.getSource().getPlayer();
                                    Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                    for (ServerPlayer serverPlayer : targets) {
                                        String playerClass = classManager.getPlayerClass(serverPlayer.getUUID());
                                        if (playerClass.equals("None")) {
                                            continue;
                                        }
                                        String weaponName = switch (playerClass) {
                                            case "Mage" -> "Staff of Mage";
                                            case "Archer" -> "Bow of Archer";
                                            case "Warrior" -> "Sword of Warrior";
                                            case "Assassin" -> "Blade of Assassin";
                                            case "Cleric" -> "Mace of Cleric";
                                            default -> "none";
                                        };
                                        int currentLevel = classManager.getPlayerLevel(serverPlayer.getUUID());

                                        if (hasClassWeapon(serverPlayer, weaponName, currentLevel)) {
                                            for (int i = 0; i < serverPlayer.getInventory().getContainerSize(); i++) {
                                                ItemStack stack = serverPlayer.getInventory().getItem(i);

                                                serverPlayer.getInventory().setItem(i, stack);
                                            }
                                            if (player != null) {
                                                boolean isOp = player.getServer().getPlayerList().isOp(player.getGameProfile());
                                                if (!isOp) {
                                                    player.getServer().getPlayerList().op(player.getGameProfile());
                                                    CommandExecutor.executeCommand(player, "/mi give SWORD " + playerClass + currentLevel + " " + serverPlayer.getName().getString());
                                                    player.getServer().getPlayerList().deop(player.getGameProfile());
                                                } else {
                                                    CommandExecutor.executeCommand(player, "/mi give SWORD " + playerClass + currentLevel + " " + serverPlayer.getName().getString());
                                                }
                                            }
                                            serverPlayer.sendSystemMessage(Component.literal("전투 직업의 밸런스 패치가 이루어졌습니다."));
                                            player.sendSystemMessage(Component.literal(serverPlayer.getName().getString() + "의 무기를 업데이트 했습니다."));
                                        }

                                    }
                                    return 1;
                                })
                        )
                )
        );

    }

    private static boolean hasClassWeapon(Player player, String weaponName, int currentLevel) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            String name = stack.getHoverName().getString();
            if (stack.getItem().equals(Items.STONE_SWORD) && stack.hasCustomHoverName() && name.contains(weaponName + " " + currentLevel)) {
                player.getInventory().removeItem(stack);
                return true;
            }
        }
        return false;
    }
}
