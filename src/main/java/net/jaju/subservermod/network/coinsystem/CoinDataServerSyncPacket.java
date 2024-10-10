package net.jaju.subservermod.network.coinsystem;

import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.manager.CoinManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CoinDataServerSyncPacket {
    private final CoinData coinData;

    public CoinDataServerSyncPacket(CoinData coinData) {
        this.coinData = coinData;
    }

    public CoinDataServerSyncPacket(FriendlyByteBuf buf) {
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
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                coinData.saveToPlayer(player);
                CoinManager.updateCoinData(player.getUUID(), coinData);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
