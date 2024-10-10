package net.jaju.subservermod.network.mailbox;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class ClientPacketHandler {
    private static Map<UUID, List<JsonObject>> mailboxes;

    public static void handleMailboxDataPacket(Map<UUID, List<JsonObject>> receivedMailboxes) {
        Minecraft.getInstance().execute(() -> {
            mailboxes = receivedMailboxes;
        });
    }

    public static Map<UUID, List<JsonObject>> getMailboxes() {
        return mailboxes;
    }
}