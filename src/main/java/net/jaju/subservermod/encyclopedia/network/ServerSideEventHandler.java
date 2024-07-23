package net.jaju.subservermod.encyclopedia.network;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.encyclopedia.EncyclopediaManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ServerSideEventHandler {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID playerUUID = event.getEntity().getUUID();
            EncyclopediaManager manager = EncyclopediaManager.getInstance();
            manager.initializePlayerDiscoveries(playerUUID);
        }
    }

//    @SubscribeEvent
//    public static void onServerTick(TickEvent.ServerTickEvent event) {
//        if (event.phase == TickEvent.Phase.END) {
//            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
//                sendEncyclopediaData(player);
//            }
//        }
//    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (player instanceof ServerPlayer serverPlayer) {
                sendEncyclopediaData(serverPlayer);
            }
        }
    }

    private static void sendEncyclopediaData(ServerPlayer player) {
        LinkedHashMap<String, Integer> encyclopedia = EncyclopediaManager.getInstance().getEncyclopedia();
        HashMap<String, Boolean> discoveries = EncyclopediaManager.getInstance().getDiscoveries(player.getUUID());
        LinkedHashMap<Integer, List<ItemStack>> giftList = EncyclopediaManager.getInstance().getGiftList();
        LinkedHashMap<Integer, Boolean> giftGet = EncyclopediaManager.getInstance().getGiftGet(player.getUUID());
        EncyclopediaPacket packet = new EncyclopediaPacket(encyclopedia, discoveries, giftList, giftGet);
        ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
