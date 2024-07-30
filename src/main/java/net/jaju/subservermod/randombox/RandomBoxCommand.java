package net.jaju.subservermod.randombox;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.jaju.subservermod.Subservermod;
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
                                        ItemStack randomBoxItem = new ItemStack(ModItem.RANDOMBOX.get());

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
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    RandomBox box = randomBoxes.get(name);

                                    if (box != null) {
                                        context.getSource().sendSuccess(() -> Component.literal("랜덤 박스 '" + name + "'의 아이템 목록:"), false);
                                        box.getItems().forEach(item -> {
                                            context.getSource().sendSuccess(() -> Component.literal(item.getItem().getDisplayName().getString() + " - 확률: " + item.getChance() + "%"), false);
                                        });
                                    } else {
                                        context.getSource().sendFailure(Component.literal("랜덤 박스를 찾을 수 없습니다."));
                                    }

                                    return 1;
                                })))
        );
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

    private static void loadRandomBoxes() {
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