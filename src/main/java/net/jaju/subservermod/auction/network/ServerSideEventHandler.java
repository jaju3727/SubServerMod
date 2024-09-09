package net.jaju.subservermod.auction.network;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.auction.AuctionManager;
import net.jaju.subservermod.auction.network.packet.AuctionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

@Mod.EventBusSubscriber
public class ServerSideEventHandler {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID playerUUID = event.getEntity().getUUID();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (player instanceof ServerPlayer serverPlayer) {
                sendAuctionData(serverPlayer);
            }
        }
    }

    private static void sendAuctionData(ServerPlayer player) {
        AuctionPacket packet = new AuctionPacket(AuctionManager.getInstance().getAuctionItems());
        ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
