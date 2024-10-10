package net.jaju.subservermod.commands.coinsystem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.manager.CoinManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.jaju.subservermod.Subservermod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class CoinSendCommand {
    private static final List<String> COIN_TYPES = Arrays.asList(
            "subcoin", "chefcoin", "farmercoin", "fishermancoin",
            "alchemistcoin", "minercoin", "woodcuttercoin"
    );

    private static final SuggestionProvider<CommandSourceStack> COIN_TYPE_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(COIN_TYPES, builder);

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("송금")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("coinType", StringArgumentType.string())
                        .suggests(COIN_TYPE_SUGGESTIONS)
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            ServerPlayer sender = context.getSource().getPlayerOrException();
                                            ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                            String coinType = StringArgumentType.getString(context, "coinType");
                                            int amount = IntegerArgumentType.getInteger(context, "amount");

                                            if (sendCoins(sender, target, coinType, amount)) {
                                                context.getSource().sendSuccess(() -> Component.literal("성공적으로 " + target.getName().getString() + " 에게 " + amount + "개의 " + coinType + " 를 보냈습니다."), true);
                                            } else {
                                                context.getSource().sendFailure(Component.literal("코인 전송에 실패했습니다."));
                                            }

                                            return 1;
                                        })))));
    }

    private static boolean sendCoins(ServerPlayer sender, ServerPlayer target, String coinType, int amount) {
        CoinData senderData = CoinManager.getCoinData(sender);
        CoinData targetData = CoinManager.getCoinData(target);

        int senderCoins = getCoinAmount(senderData, coinType);
        if (senderCoins < amount) {
            sender.sendSystemMessage(Component.literal("보유한 " + coinType + " 코인이 부족합니다."));
            return false;
        }

        setCoinAmount(senderData, coinType, senderCoins - amount);
        setCoinAmount(targetData, coinType, getCoinAmount(targetData, coinType) + amount);

        senderData.saveToPlayer(sender);
        targetData.saveToPlayer(target);

        return true;
    }

    private static int getCoinAmount(CoinData coinData, String coinType) {
        return switch (coinType.toLowerCase()) {
            case "subcoin" -> coinData.getSubcoin();
            case "chefcoin" -> coinData.getChefcoin();
            case "farmercoin" -> coinData.getFarmercoin();
            case "fishermancoin" -> coinData.getFishermancoin();
            case "alchemistcoin" -> coinData.getAlchemistcoin();
            case "minercoin" -> coinData.getMinercoin();
            case "woodcuttercoin" -> coinData.getWoodcuttercoin();
            default -> 0;
        };
    }

    private static void setCoinAmount(CoinData coinData, String coinType, int amount) {
        switch (coinType.toLowerCase()) {
            case "subcoin" -> coinData.setSubcoin(amount);
            case "chefcoin" -> coinData.setChefcoin(amount);
            case "farmercoin" -> coinData.setFarmercoin(amount);
            case "fishermancoin" -> coinData.setFishermancoin(amount);
            case "alchemistcoin" -> coinData.setAlchemistcoin(amount);
            case "minercoin" -> coinData.setMinercoin(amount);
            case "woodcuttercoin" -> coinData.setWoodcuttercoin(amount);
        }
    }
}
