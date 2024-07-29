package net.jaju.subservermod.randombox;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.jaju.subservermod.item.ModItem;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RandomBoxCommand {
    private static final SuggestionProvider<CommandSourceStack> RANDOM_BOX_SUGGESTIONS =
            (context, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(RandomBoxManager.getRandomBoxes().keySet(), builder);

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("randombox")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    RandomBoxManager.getRandomBoxes().put(name, new RandomBox(name));
                                    context.getSource().sendSuccess(() -> Component.literal("랜덤 박스 '" + name + "' 생성됨"), true);
                                    RandomBoxManager.saveRandomBoxes();
                                    return 1;
                                })))
                .then(Commands.literal("additem")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(RANDOM_BOX_SUGGESTIONS)
                                .then(Commands.argument("chance", IntegerArgumentType.integer(1, 100))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack itemStack = player.getMainHandItem();
                                            String name = StringArgumentType.getString(context, "name");
                                            int chance = IntegerArgumentType.getInteger(context, "chance");

                                            RandomBox box = RandomBoxManager.getRandomBoxes().get(name);
                                            if (box != null && !itemStack.isEmpty()) {
                                                box.addItem(itemStack.copy(), chance);
                                                context.getSource().sendSuccess(() -> Component.literal("아이템이 랜덤 박스 '" + name + "'에 추가됨"), true);
                                                RandomBoxManager.saveRandomBoxes();
                                            } else {
                                                context.getSource().sendFailure(Component.literal("랜덤 박스를 찾을 수 없거나 빈 아이템입니다."));
                                            }
                                            return 1;
                                        }))))
                .then(Commands.literal("get")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(RANDOM_BOX_SUGGESTIONS)
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    String name = StringArgumentType.getString(context, "name");

                                    RandomBox box = RandomBoxManager.getRandomBoxes().get(name);
                                    if (box != null) {
                                        ItemStack randomBoxItem = new ItemStack(ModItem.RANDOMBOX.get());

                                        MutableComponent lore = Component.literal(name);
                                        ListTag loreList = new ListTag();
                                        loreList.add(StringTag.valueOf(Component.Serializer.toJson(lore)));
                                        randomBoxItem.getOrCreateTagElement("display").put("Lore", loreList);

                                        player.addItem(randomBoxItem);

                                        player.addItem(randomBoxItem);
                                        player.sendSystemMessage(Component.literal("랜덤 박스 아이템 '" + name + "'을(를) 얻었습니다."));
                                    } else {
                                        context.getSource().sendFailure(Component.literal("랜덤 박스를 찾을 수 없습니다."));
                                    }
                                    return 1;
                                })))
                .then(Commands.literal("list")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(RANDOM_BOX_SUGGESTIONS)
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    RandomBox box = RandomBoxManager.getRandomBoxes().get(name);

                                    if (box != null) {
                                        context.getSource().sendSuccess(() -> Component.literal("랜덤 박스 '" + name + "'의 아이템 목록:"), false);
                                        for (RandomBox.RandomBoxItem item : box.getItems()) {
                                            context.getSource().sendSuccess(() -> Component.literal(item.getItem().getHoverName().getString() + " - 확률: " + item.getChance()), false);
                                        }
                                    } else {
                                        context.getSource().sendFailure(Component.literal("랜덤 박스를 찾을 수 없습니다."));
                                    }

                                    return 1;
                                })))
        );
    }

    public static RandomBox getRandomBox(String name) {
        return RandomBoxManager.getRandomBoxes().get(name);
    }
}
