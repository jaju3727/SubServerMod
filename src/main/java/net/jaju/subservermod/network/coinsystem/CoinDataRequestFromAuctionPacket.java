package net.jaju.subservermod.network.coinsystem;

import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.manager.CoinManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class CoinDataRequestFromAuctionPacket {

    public CoinDataRequestFromAuctionPacket() {
    }

    public CoinDataRequestFromAuctionPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                CoinData coinData = CoinManager.getCoinData(player);
                ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CoinDataAuctionSyncPacket(coinData));
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
