package net.jaju.subservermod.network.mailbox.packet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.jaju.subservermod.manager.MailboxManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class UpdateMailboxPacket {
    private final UUID playerUUID;
    private final List<JsonObject> updatedMailboxItems;

    public UpdateMailboxPacket(UUID playerUUID, List<JsonObject> updatedMailboxItems) {
        this.playerUUID = playerUUID;
        this.updatedMailboxItems = updatedMailboxItems;
    }

    public static void encode(UpdateMailboxPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeInt(msg.updatedMailboxItems.size());
        for (JsonObject jsonObject : msg.updatedMailboxItems) {
            buf.writeUtf(jsonObject.toString());
        }
    }

    public static UpdateMailboxPacket decode(FriendlyByteBuf buf) {
        UUID playerUUID = buf.readUUID();
        int size = buf.readInt();
        List<JsonObject> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            items.add(JsonParser.parseString(buf.readUtf()).getAsJsonObject());
        }
        return new UpdateMailboxPacket(playerUUID, items);
    }

    public static void handle(UpdateMailboxPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender().getServer().getPlayerList().getPlayer(msg.playerUUID);
            if (player != null) {
                MailboxManager.getInstance().updateMailboxItems(player.getUUID(), msg.updatedMailboxItems);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}