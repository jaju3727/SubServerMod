package net.jaju.subservermod.landsystem.network;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.landsystem.LandManager;
import net.jaju.subservermod.landsystem.network.packet.ChunkOwnerUpdatePacket;
import net.jaju.subservermod.landsystem.network.packet.ChunkOwnersPacket;
import net.jaju.subservermod.landsystem.network.packet.ChunkSharersPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ServerSideEventHandler {
    private static final Map<UUID, UUID> playerInChunk = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {

                ChunkOwnersPacket packetOwners = new ChunkOwnersPacket(LandManager.getInstance().getChunkOwners());
                ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packetOwners);
                ChunkSharersPacket packetSharers = new ChunkSharersPacket(LandManager.getInstance().getChunkSharers());
                ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packetSharers);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            Level world = player.level();
            BlockPos pos = player.blockPosition();
            ChunkPos chunkPos = new ChunkPos(pos);
            ResourceKey<Level> worldKey = world.dimension();
            String chunkKey = LandManager.getInstance().getChunkKey(worldKey, chunkPos, pos.getY());
            UUID owner = LandManager.getInstance().getOwner(chunkKey);


            if (player instanceof ServerPlayer serverPlayer) {
                ChunkOwnerUpdatePacket packet;
                String ownerName = LandManager.getInstance().getPlayerName(owner);
                if (owner != null) {
                    packet = new ChunkOwnerUpdatePacket(player.getUUID(), owner, ownerName);
                } else {
                    packet = new ChunkOwnerUpdatePacket(player.getUUID(), null, ownerName);
                }
                ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
            }
        }
    }
}
