package net.jaju.subservermod.mailbox;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.jaju.subservermod.encyclopedia.EncyclopediaManager;
import net.jaju.subservermod.util.ItemStackSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@Mod.EventBusSubscriber
public class MailboxManager {
    private static MailboxManager INSTANCE;
    private static final String FILE_PATH = "config/mailbox.json";
    private final Gson gson = new Gson();
    private final Map<UUID, List<JsonObject>> mailboxes = new HashMap<>();

    public MailboxManager() {
        loadMailboxes();
    }

    public static MailboxManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MailboxManager();
        }
        return INSTANCE;
    }

    public Map<UUID, List<JsonObject>> getMailbox() {
        return mailboxes;
    }

    public void addItemToMailbox(UUID playerUUID, ItemStack itemStack) {
        JsonObject serializedItem = ItemStackSerializer.serialize(itemStack);
        mailboxes.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(serializedItem);
        saveMailboxes();
        sendMessageToPlayer(playerUUID, Component.literal("아이템이 우편함에 추가되었습니다."));
    }

    private void sendMessageToPlayer(UUID playerUUID, Component message) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);
            if (player != null) {
                player.sendSystemMessage(message);
            }
        }
    }

    void saveMailboxes() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(mailboxes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMailboxes() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<Map<UUID, List<JsonObject>>>() {}.getType();
            Map<UUID, List<JsonObject>> loadedMailboxes = gson.fromJson(reader, type);
            if (loadedMailboxes != null) {
                mailboxes.putAll(loadedMailboxes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMailboxItems(UUID playerUUID, List<JsonObject> items) {
        mailboxes.put(playerUUID, items);
        saveMailboxes();
    }
}
