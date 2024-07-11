package net.jaju.subservermod.coinsystem.network;

import net.jaju.subservermod.coinsystem.CoinData;
import net.jaju.subservermod.coinsystem.CoinHud;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CoinDataSyncPacket {
    private final CoinData coinData;

    public CoinDataSyncPacket(CoinData coinData) {
        this.coinData = coinData;
    }

    public CoinDataSyncPacket(FriendlyByteBuf buf) {
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

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            CoinHud.updateCoinMap(coinData);
        });
        ctx.get().setPacketHandled(true);
    }
}
