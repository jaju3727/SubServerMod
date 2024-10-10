package net.jaju.subservermod.network.auction;

import net.jaju.subservermod.util.AuctionItem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class ClientAuctionHandler {
    private static List<AuctionItem> auctionItems;

    public static void handleAuctionDataPacket(List<AuctionItem> receivedAuctionItems) {
        Minecraft.getInstance().execute(() -> {
            auctionItems = receivedAuctionItems;
        });
    }

    public static List<AuctionItem> getAuctionItems() {
        return auctionItems;
    }
}
