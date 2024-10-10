package net.jaju.subservermod.network.auction.packet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.jaju.subservermod.util.AuctionItem;
import net.jaju.subservermod.network.auction.ClientAuctionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class AuctionPacket {
    private final List<AuctionItem> auctionItems;

    public AuctionPacket(List<AuctionItem> auctionItems) {
        this.auctionItems = auctionItems;
    }

    public static void encode(AuctionPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.auctionItems.size());
        for (AuctionItem item : msg.auctionItems) {
            buf.writeLong(item.timestamp());
            buf.writeUtf(item.playerName());
            buf.writeUtf(item.item().toString());
            buf.writeUtf(item.coinType());
            buf.writeInt(item.coinNum());
            buf.writeUUID(item.playerUUID());
        }
    }

    public static AuctionPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<AuctionItem> auctionItems = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            long timestamp = buf.readLong();
            String playerName = buf.readUtf();
            JsonObject item = JsonParser.parseString(buf.readUtf()).getAsJsonObject();
            String coinType = buf.readUtf();
            int coinNum = buf.readInt();
            UUID playerUUID = buf.readUUID();
            auctionItems.add(new AuctionItem(timestamp, playerName, item, coinType, coinNum, playerUUID));
        }
        return new AuctionPacket(auctionItems);
    }

    public static void handle(AuctionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientAuctionHandler.handleAuctionDataPacket(msg.auctionItems);
        });
        ctx.get().setPacketHandled(true);
    }
}