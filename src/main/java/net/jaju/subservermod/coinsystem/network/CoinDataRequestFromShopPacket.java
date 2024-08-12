package net.jaju.subservermod.coinsystem.network;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.coinsystem.CoinData;
import net.jaju.subservermod.coinsystem.CoinHud;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class CoinDataRequestFromShopPacket {

    public CoinDataRequestFromShopPacket() {
    }

    public CoinDataRequestFromShopPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                CoinData coinData = CoinHud.getCoinData(player);
                ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CoinDataShopSyncPacket(coinData));
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}