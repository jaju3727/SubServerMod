package net.jaju.subservermod.commands.coinsystem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.manager.CoinManager;
import net.jaju.subservermod.items.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class CoinWithdrawCommand {

    private static final List<String> COIN_TYPES = Arrays.asList(
            "subcoin", "chefcoin", "farmercoin", "fishermancoin",
            "alchemistcoin", "minercoin", "woodcuttercoin"
    );

    private static final SuggestionProvider<CommandSourceStack> COIN_TYPE_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(COIN_TYPES, builder);

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("출금")
                .then(Commands.argument("coinType", StringArgumentType.string())
                .suggests(COIN_TYPE_SUGGESTIONS)
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    String coinType = StringArgumentType.getString(context, "coinType");
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    ServerPlayer player = context.getSource().getPlayerOrException();

                                    CoinData coinData = CoinManager.getCoinData(player);

                                    int playerCoins = switch (coinType) {
                                        case "subcoin" -> coinData.getSubcoin();
                                        case "chefcoin" -> coinData.getChefcoin();
                                        case "farmercoin" -> coinData.getFarmercoin();
                                        case "fishermancoin" -> coinData.getFishermancoin();
                                        case "alchemistcoin" -> coinData.getAlchemistcoin();
                                        case "minercoin" -> coinData.getMinercoin();
                                        case "woodcuttercoin" -> coinData.getWoodcuttercoin();
                                        default -> 0;
                                    };

                                    if (playerCoins >= amount) {
                                        ItemStack coinItem = switch (coinType) {
                                            case "subcoin" -> new ItemStack(ModItems.SUB_COIN.get());
                                            case "chefcoin" -> new ItemStack(ModItems.CHEF_COIN.get());
                                            case "farmercoin" -> new ItemStack(ModItems.FARMER_COIN.get());
                                            case "fishermancoin" -> new ItemStack(ModItems.FISHERMAN_COIN.get());
                                            case "alchemistcoin" -> new ItemStack(ModItems.ALCHEMIST_COIN.get());
                                            case "minercoin" -> new ItemStack(ModItems.MINER_COIN.get());
                                            case "woodcuttercoin" -> new ItemStack(ModItems.WOODCUTTER_COIN.get());
                                            default -> throw new IllegalStateException("Unexpected value: " + coinType);
                                        };
                                        MutableComponent lore = Component.literal(amount + "원");
                                        ListTag loreList = new ListTag();
                                        loreList.add(StringTag.valueOf(Component.Serializer.toJson(lore)));
                                        coinItem.getOrCreateTagElement("display").put("Lore", loreList);
                                        player.addItem(coinItem);

                                        switch (coinType) {
                                            case "subcoin" -> coinData.setSubcoin(playerCoins - amount);
                                            case "chefcoin" -> coinData.setChefcoin(playerCoins - amount);
                                            case "farmercoin" -> coinData.setFarmercoin(playerCoins - amount);
                                            case "fishermancoin" -> coinData.setFishermancoin(playerCoins - amount);
                                            case "alchemistcoin" -> coinData.setAlchemistcoin(playerCoins - amount);
                                            case "minercoin" -> coinData.setMinercoin(playerCoins - amount);
                                            case "woodcuttercoin" -> coinData.setWoodcuttercoin(playerCoins - amount);
                                        }

                                        coinData.saveToPlayer(player);
                                        player.sendSystemMessage(Component.literal(coinType + "에서 " + amount + "원을 인출했습니다."));
                                        return 1;
                                    } else {
                                        player.sendSystemMessage(Component.literal("보유한 코인이 부족합니다."));
                                        return 0;
                                    }
                                })
                        )
                ));
    }
}
