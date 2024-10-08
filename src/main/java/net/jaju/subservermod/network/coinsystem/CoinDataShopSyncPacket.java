package net.jaju.subservermod.network.coinsystem;

import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.screen.shopsystem.ShopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CoinDataShopSyncPacket {
    private final CoinData coinData;

    public CoinDataShopSyncPacket(CoinData coinData) {
        this.coinData = coinData;
    }

    public CoinDataShopSyncPacket(FriendlyByteBuf buf) {
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
                if (mc.screen instanceof ShopScreen shopScreen) {
                    shopScreen.updateCoinData(coinData);
                }
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
