package net.jaju.subservermod.mailbox.network.packet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.jaju.subservermod.mailbox.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class MailboxPacket {
    private final Map<UUID, List<JsonObject>> mailboxes;

    public MailboxPacket(Map<UUID, List<JsonObject>> mailboxes) {
        this.mailboxes = mailboxes;
    }

    public static void encode(MailboxPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.mailboxes.size());
        for (Map.Entry<UUID, List<JsonObject>> entry : msg.mailboxes.entrySet()) {
            buf.writeUUID(entry.getKey());
            buf.writeInt(entry.getValue().size());
            for (JsonObject jsonObject : entry.getValue()) {
                buf.writeUtf(jsonObject.toString());
            }
        }
    }

    public static MailboxPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Map<UUID, List<JsonObject>> mailboxes = new HashMap<>();

        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            int listSize = buf.readInt();
            List<JsonObject> items = new ArrayList<>();

            for (int j = 0; j < listSize; j++) {
                String jsonString = buf.readUtf();
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                items.add(jsonObject);
            }
            mailboxes.put(uuid, items);
        }

        return new MailboxPacket(mailboxes);
    }

    public static void handle(MailboxPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler.handleMailboxDataPacket(msg.mailboxes);
        });
        ctx.get().setPacketHandled(true);
    }
}
