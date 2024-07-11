package net.jaju.subservermod.coinsystem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.coinsystem.network.CoinDataSyncPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class CoinCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("coin")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("type", StringArgumentType.string())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            String type = StringArgumentType.getString(context, "type");
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            setCoin(player, type, amount);
                                            context.getSource().sendSuccess(() -> Component.literal("Set " + type + " to " + amount), true);
                                            return 1;
                                        }))))
                .then(Commands.literal("add")
                        .then(Commands.argument("type", StringArgumentType.string())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            String type = StringArgumentType.getString(context, "type");
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            addCoin(player, type, amount);
                                            context.getSource().sendSuccess(() -> Component.literal("Added " + amount + " to " + type), true);
                                            return 1;
                                        }))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("type", StringArgumentType.string())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            String type = StringArgumentType.getString(context, "type");
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            removeCoin(player, type, amount);
                                            context.getSource().sendSuccess(() -> Component.literal("Removed " + amount + " from " + type), true);
                                            return 1;
                                        }))))
        );
    }

    private static void setCoin(ServerPlayer player, String type, int amount) {
        CoinData coinData = CoinHud.getCoinData(player);
        switch (type) {
            case "subcoin" -> coinData.setSubcoin(amount);
            case "chefcoin" -> coinData.setChefcoin(amount);
            case "farmercoin" -> coinData.setFarmercoin(amount);
            case "fishermancoin" -> coinData.setFishermancoin(amount);
            case "alchemistcoin" -> coinData.setAlchemistcoin(amount);
            case "minercoin" -> coinData.setMinercoin(amount);
            case "woodcuttercoin" -> coinData.setWoodcuttercoin(amount);
        }
        coinData.saveToPlayer(player);
        ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CoinDataSyncPacket(coinData));
    }

    private static void addCoin(ServerPlayer player, String type, int amount) {
        CoinData coinData = CoinHud.getCoinData(player);
        switch (type) {
            case "subcoin" -> coinData.setSubcoin(coinData.getSubcoin() + amount);
            case "chefcoin" -> coinData.setChefcoin(coinData.getChefcoin() + amount);
            case "farmercoin" -> coinData.setFarmercoin(coinData.getFarmercoin() + amount);
            case "fishermancoin" -> coinData.setFishermancoin(coinData.getFishermancoin() + amount);
            case "alchemistcoin" -> coinData.setAlchemistcoin(coinData.getAlchemistcoin() + amount);
            case "minercoin" -> coinData.setMinercoin(coinData.getMinercoin() + amount);
            case "woodcuttercoin" -> coinData.setWoodcuttercoin(coinData.getWoodcuttercoin() + amount);
        }
        coinData.saveToPlayer(player);
        ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CoinDataSyncPacket(coinData));
    }

    private static void removeCoin(ServerPlayer player, String type, int amount) {
        CoinData coinData = CoinHud.getCoinData(player);
        switch (type) {
            case "subcoin" -> coinData.setSubcoin(coinData.getSubcoin() - amount);
            case "chefcoin" -> coinData.setChefcoin(coinData.getChefcoin() - amount);
            case "farmercoin" -> coinData.setFarmercoin(coinData.getFarmercoin() - amount);
            case "fishermancoin" -> coinData.setFishermancoin(coinData.getFishermancoin() - amount);
            case "alchemistcoin" -> coinData.setAlchemistcoin(coinData.getAlchemistcoin() - amount);
            case "minercoin" -> coinData.setMinercoin(coinData.getMinercoin() - amount);
            case "woodcuttercoin" -> coinData.setWoodcuttercoin(coinData.getWoodcuttercoin() - amount);
        }
        coinData.saveToPlayer(player);
        ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CoinDataSyncPacket(coinData));
    }
}
