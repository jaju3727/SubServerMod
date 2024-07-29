package net.jaju.subservermod.coinsystem.network;

import net.jaju.subservermod.auction.AuctionScreen;
import net.jaju.subservermod.coinsystem.CoinData;
import net.jaju.subservermod.shopsystem.screen.ShopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CoinDataAuctionSyncPacket {
    private final CoinData coinData;

    public CoinDataAuctionSyncPacket(CoinData coinData) {
        this.coinData = coinData;
    }

    public CoinDataAuctionSyncPacket(FriendlyByteBuf buf) {
        this.coinData = new CoinData();
        this.coinData.setSubcoin(buf.readInt());
        this.coinData.setChefcoin(buf.readInt());
        this.coinData.setFarmercoin(buf.readInt());
        this.coinData.setFishermancoin(buf.readInt());
        this.coinData.setAlchemistcoin(buf.readInt());
        this.coinData.setMinercoin(buf.readInt());
        this.coinData.setWoodcuttercoin(buf.readInt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(coinData.getSubcoin());
        buf.writeInt(coinData.getChefcoin());
        buf.writeInt(coinData.getFarmercoin());
        buf.writeInt(coinData.getFishermancoin());
        buf.writeInt(coinData.getAlchemistcoin());
        buf.writeInt(coinData.getMinercoin());
        buf.writeInt(coinData.getWoodcuttercoin());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                if (mc.screen instanceof AuctionScreen auctionScreen) {
                    auctionScreen.updateCoinData(coinData);
                }
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
