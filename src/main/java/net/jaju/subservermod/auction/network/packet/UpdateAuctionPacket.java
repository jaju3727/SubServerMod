package net.jaju.subservermod.auction.network.packet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.jaju.subservermod.auction.AuctionItem;
import net.jaju.subservermod.auction.AuctionManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class UpdateAuctionPacket {
    private final List<AuctionItem> updatedAuctionItems;

    public UpdateAuctionPacket(List<AuctionItem> updatedAuctionItems) {
        this.updatedAuctionItems = updatedAuctionItems;
    }

    public static void encode(UpdateAuctionPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.updatedAuctionItems.size());
        for (AuctionItem item : msg.updatedAuctionItems) {
            buf.writeLong(item.timestamp());
            buf.writeUtf(item.playerName());
            buf.writeUtf(item.item().toString());
            buf.writeUtf(item.coinType());
            buf.writeInt(item.coinNum());
            buf.writeUUID(item.playerUUID());
        }
    }

    public static UpdateAuctionPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<AuctionItem> updatedAuctionItems = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            long timestamp = buf.readLong();
            String playerName = buf.readUtf();
            JsonObject item = JsonParser.parseString(buf.readUtf()).getAsJsonObject();
            String coinType = buf.readUtf();
            int coinNum = buf.readInt();
            UUID playerUUID = buf.readUUID();
            updatedAuctionItems.add(new AuctionItem(timestamp, playerName, item, coinType, coinNum, playerUUID));
        }
        return new UpdateAuctionPacket(updatedAuctionItems);
    }

    public static void handle(UpdateAuctionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            AuctionManager.getInstance().updateAuctionItems(msg.updatedAuctionItems);
        });
        ctx.get().setPacketHandled(true);
    }
}
