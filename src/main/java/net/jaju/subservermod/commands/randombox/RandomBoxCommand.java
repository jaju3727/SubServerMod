package net.jaju.subservermod.commands.randombox;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.items.ModItems;
import net.jaju.subservermod.util.RandomBox;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class RandomBoxCommand {
    private static final Map<String, RandomBox> randomBoxes = new HashMap<>();
    private static final Gson gson = new Gson();
    private static final String FILE_PATH = "config/random_boxes.json";

    static {
        loadRandomBoxes();
    }

    private static final SuggestionProvider<CommandSourceStack> RANDOM_BOX_SUGGESTIONS =
            (context, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(randomBoxes.keySet(), builder);

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("randombox")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    randomBoxes.put(name, new RandomBox(name));
                                    saveRandomBoxes();
                                    context.getSource().sendSuccess(() -> Component.literal("랜덤 박스 '" + name + "' 생성됨"), true);
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

                                            RandomBox box = randomBoxes.get(name);
                                            if (box != null && !itemStack.isEmpty()) {
                                                box.addItem(itemStack.copy(), chance);
                                                saveRandomBoxes();
                                                context.getSource().sendSuccess(() -> Component.literal("아이템이 랜덤 박스 '" + name + "'에 추가됨"), true);
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

                                    RandomBox box = randomBoxes.get(name);
                                    if (box != null) {
                                        ItemStack randomBoxItem = new ItemStack(ModItems.RANDOMBOX.get());

                                        MutableComponent lore = Component.literal(name);
                                        ListTag loreList = new ListTag();
                                        loreList.add(StringTag.valueOf(Component.Serializer.toJson(lore)));
                                        randomBoxItem.getOrCreateTagElement("display").put("Lore", loreList);

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
                                .executes(context -> listRandomBoxItems(context, 0))
                                .then(Commands.argument("page", IntegerArgumentType.integer(0))
                                        .executes(context -> listRandomBoxItems(context, IntegerArgumentType.getInteger(context, "page"))))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(RANDOM_BOX_SUGGESTIONS)
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");

                                    if (randomBoxes.remove(name) != null) {
                                        saveRandomBoxes();
                                        context.getSource().sendSuccess(() -> Component.literal("랜덤 박스 '" + name + "'이(가) 삭제되었습니다."), true);
                                    } else {
                                        context.getSource().sendFailure(Component.literal("랜덤 박스를 찾을 수 없습니다."));
                                    }

                                    return 1;
                                })
                                .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            int index = IntegerArgumentType.getInteger(context, "index");

                                            RandomBox box = randomBoxes.get(name);
                                            if (box != null) {
                                                if (index >= 0 && index < box.getItems().size()) {
                                                    RandomBox.RandomBoxItem removedItem = box.getItems().remove(index);
                                                    saveRandomBoxes();
                                                    context.getSource().sendSuccess(() -> Component.literal("아이템 '" + removedItem.getItem().getDisplayName().getString() + "'이(가) 랜덤 박스 '" + name + "'에서 삭제되었습니다."), true);
                                                } else {
                                                    context.getSource().sendFailure(Component.literal("해당 인덱스에 아이템이 없습니다."));
                                                }
                                            } else {
                                                context.getSource().sendFailure(Component.literal("랜덤 박스를 찾을 수 없습니다."));
                                            }

                                            return 1;
                                        }))))

        );
    }

    private static int listRandomBoxItems(CommandContext<CommandSourceStack> context, int page) {
        String name = StringArgumentType.getString(context, "name");
        RandomBox box = randomBoxes.get(name);

        if (box != null) {
            int itemsPerPage = 10;
            int startIndex = page * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, box.getItems().size());

            if (startIndex >= box.getItems().size()) {
                context.getSource().sendFailure(Component.literal("해당 페이지에 아이템이 없습니다."));
                return 0;
            }

            context.getSource().sendSuccess(() -> Component.literal("랜덤 박스 '" + name + "'의 아이템 목록 (페이지 " + (page + 1) + "):"), false);

            for (int i = startIndex; i < endIndex; i++) {
                RandomBox.RandomBoxItem item = box.getItems().get(i);
                int finalI = i;
                context.getSource().sendSuccess(() -> Component.literal(finalI + ": " + item.getItem().getDisplayName().getString() + " - 확률: " + item.getChance() + "%"), false);
            }

            if (endIndex < box.getItems().size()) {
                MutableComponent nextPage = Component.literal("[다음 페이지]").withStyle(Style.EMPTY
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("다음 페이지로 이동")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/randombox list " + name + " " + (page + 1))));
                context.getSource().sendSuccess(() -> nextPage, false);
            }

        } else {
            context.getSource().sendFailure(Component.literal("랜덤 박스를 찾을 수 없습니다."));
        }

        return 1;
    }

    public static RandomBox getRandomBox(String name) {
        return randomBoxes.get(name);
    }

    private static void saveRandomBoxes() {
        JsonArray jsonArray = new JsonArray();
        for (RandomBox box : randomBoxes.values()) {
            jsonArray.add(box.serialize());
        }
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadRandomBoxes() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<JsonArray>() {}.getType();
            JsonArray jsonArray = gson.fromJson(reader, type);
            if (jsonArray != null) {
                for (JsonElement element : jsonArray) {
                    RandomBox box = RandomBox.deserialize(element.getAsJsonObject());
                    randomBoxes.put(box.getName(), box);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}