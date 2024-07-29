package net.jaju.subservermod.coinsystem.network;

import net.jaju.subservermod.coinsystem.CoinData;
import net.jaju.subservermod.coinsystem.CoinHud;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CoinDataUpdatePacket {
    private final CoinData coinData;
    private final UUID playerUUID;

    public CoinDataUpdatePacket(CoinData coinData, UUID playerUUID) {
        this.coinData = coinData;
        this.playerUUID = playerUUID;
    }

    public CoinDataUpdatePacket(FriendlyByteBuf buf) {
        this.coinData = new CoinData();
        this.coinData.setSubcoin(buf.readInt());
        this.coinData.setChefcoin(buf.readInt());
        this.coinData.setFarmercoin(buf.readInt());
        this.coinData.setFishermancoin(buf.readInt());
        this.coinData.setAlchemistcoin(buf.readInt());
        this.coinData.setMinercoin(buf.readInt());
        this.coinData.setWoodcuttercoin(buf.readInt());
        this.playerUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(coinData.getSubcoin());
        buf.writeInt(coinData.getChefcoin());
        buf.writeInt(coinData.getFarmercoin());
        buf.writeInt(coinData.getFishermancoin());
        buf.writeInt(coinData.getAlchemistcoin());
        buf.writeInt(coinData.getMinercoin());
        buf.writeInt(coinData.getWoodcuttercoin());
        buf.writeUUID(playerUUID);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.getUUID().equals(playerUUID)) {
                coinData.saveToPlayer(player);
                CoinHud.updateCoinData(player.getUUID(), coinData);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
