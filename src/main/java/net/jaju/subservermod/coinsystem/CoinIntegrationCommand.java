package net.jaju.subservermod.coinsystem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CoinIntegrationCommand {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("섭")
                        .then(Commands.argument("num", IntegerArgumentType.integer(1))
                                .executes(context -> integrateCoins(context.getSource(), IntegerArgumentType.getInteger(context, "num")))
                        )
        );
    }

    private static int integrateCoins(CommandSourceStack source, int num) {
        ServerPlayer player = source.getPlayer();
        CoinData coinData = CoinHud.getCoinData(player);

        // 각 코인의 잔고 확인
        int availableSubcoins = Math.min(num,
                Math.min(coinData.getChefcoin(),
                        Math.min(coinData.getFarmercoin(),
                                Math.min(coinData.getFishermancoin(),
                                        Math.min(coinData.getAlchemistcoin(),
                                                Math.min(coinData.getMinercoin(),
                                                        coinData.getWoodcuttercoin()))))));

        if (availableSubcoins < num) {
            player.sendSystemMessage(Component.literal("다른 코인이 부족합니다."));
            return 0;
        }

        coinData.setSubcoin(coinData.getSubcoin() + num);
        coinData.setChefcoin(coinData.getChefcoin() - num);
        coinData.setFarmercoin(coinData.getFarmercoin() - num);
        coinData.setFishermancoin(coinData.getFishermancoin() - num);
        coinData.setAlchemistcoin(coinData.getAlchemistcoin() - num);
        coinData.setMinercoin(coinData.getMinercoin() - num);
        coinData.setWoodcuttercoin(coinData.getWoodcuttercoin() - num);

        coinData.saveToPlayer(player);

        player.sendSystemMessage(Component.literal(num + "개의 섭코인으로 변환되었습니다."));

        return 1;
    }
}
