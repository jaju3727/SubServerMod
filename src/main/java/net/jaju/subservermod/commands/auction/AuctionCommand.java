package net.jaju.subservermod.commands.auction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.util.AuctionItem;
import net.jaju.subservermod.manager.AuctionManager;
import net.jaju.subservermod.util.ItemStackSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Mod.EventBusSubscriber
public class AuctionCommand {
    private static final List<String> COIN_TYPES = Arrays.asList(
            "sub_coin", "chef_coin", "farmer_coin", "fisherman_coin",
            "alchemist_coin", "miner_coin", "woodcutter_coin"
    );

    private static final SuggestionProvider<CommandSourceStack> COIN_TYPE_SUGGESTIONS =
            (context, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(COIN_TYPES, builder);

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        Subservermod.LOGGER.info("AuctionCommandLoading");
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("거래소")
                .then(Commands.literal("아이템추가")
                        .then(Commands.argument("itemNum", IntegerArgumentType.integer(1))
                                .then(Commands.argument("coinType", StringArgumentType.string())
                                        .suggests(COIN_TYPE_SUGGESTIONS)
                                        .then(Commands.argument("coinNum", IntegerArgumentType.integer(1))
                                                .executes(context -> {
                                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                                    int itemNum = IntegerArgumentType.getInteger(context, "itemNum");
                                                    String coinType = StringArgumentType.getString(context, "coinType");
                                                    int coinNum = IntegerArgumentType.getInteger(context, "coinNum");

                                                    ItemStack itemStack = player.getMainHandItem().copy();
                                                    if (!itemStack.isEmpty() && itemStack.getCount() >= itemNum) {
                                                        itemStack.setCount(itemNum);
                                                        AuctionManager.getInstance().addItemToAuction(player, itemStack, coinType, coinNum);
                                                        player.getMainHandItem().shrink(itemNum);
                                                        context.getSource().sendSuccess(() -> Component.literal("아이템이 경매장에 등록되었습니다."), true);
                                                    } else {
                                                        context.getSource().sendFailure(Component.literal("등록할 아이템이 부족합니다."));
                                                    }
                                                    return 1;
                                                })))))
                .then(Commands.literal("목록")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            List<AuctionItem> auctionItems = AuctionManager.getInstance().getAuctionItemsForPlayer(player.getUUID());

                            if (auctionItems.isEmpty()) {
                                context.getSource().sendFailure(Component.literal("등록된 아이템이 없습니다."));
                            } else {
                                context.getSource().sendSuccess(() -> Component.literal("등록된 아이템 목록:").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#00FF00"))), false);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초"); // 한국어 날짜 형식 지정
                                for (int i = 0; i < auctionItems.size(); i++) {
                                    AuctionItem auctionItem = auctionItems.get(i);
                                    ItemStack itemStack = ItemStackSerializer.deserialize(auctionItem.item());
                                    String formattedDate = dateFormat.format(new Date(auctionItem.timestamp()));
                                    Component itemComponent = Component.literal("")
                                            .append(Component.literal("(Index) " + (i + 1) + ": ").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF"))))
                                            .append(Component.literal(itemStack.getHoverName().getString()).setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFF00"))))
                                            .append(Component.literal("\n코인 타입: ").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF"))))
                                            .append(Component.literal(auctionItem.coinType()).setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FF00FF"))))
                                            .append(Component.literal("\n코인 수량: ").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF"))))
                                            .append(Component.literal(String.valueOf(auctionItem.coinNum())).setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FF0000"))))
                                            .append(Component.literal("\n출매일: ").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF"))))
                                            .append(Component.literal(formattedDate).setStyle(Style.EMPTY.withColor(TextColor.parseColor("#00FFFF"))));
                                    context.getSource().sendSuccess(() -> itemComponent, false);
                                }
                            }
                            return 1;
                        }))
                .then(Commands.literal("아이템제거")
                        .then(Commands.argument("index", IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    int index = IntegerArgumentType.getInteger(context, "index") - 1;

                                    boolean success = AuctionManager.getInstance().removeItemFromAuction(player.getUUID(), index);

                                    if (success) {
                                        context.getSource().sendSuccess(() -> Component.literal("아이템이 경매장에서 제거되었습니다.").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#00FF00"))), true);
                                    } else {
                                        context.getSource().sendFailure(Component.literal("아이템 제거에 실패했습니다. 인덱스를 확인하거나 ").setStyle(Style.EMPTY.withColor(TextColor.parseColor("#FF0000"))));
                                    }
                                    return 1;
                                }))));
    }
}