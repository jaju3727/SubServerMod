package net.jaju.subservermod.landsystem.network;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.landsystem.LandManager;
import net.jaju.subservermod.landsystem.network.packet.ChunkOwnerUpdatePacket;
import net.jaju.subservermod.landsystem.network.packet.ChunkOwnersPacket;
import net.jaju.subservermod.landsystem.network.packet.ChunkSharersPacket;
import net.minecraft.core.BlockPos;
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
            String chunkKey = LandManager.getInstance().getChunkKey(worldKey, chunkPos);
            UUID owner = LandManager.getInstance().getOwner(chunkKey);

            // 클라이언트로 패킷 전송
            if (player instanceof ServerPlayer serverPlayer) {
                if (owner != null) {
                    ChunkOwnerUpdatePacket packet = new ChunkOwnerUpdatePacket(player.getUUID(), owner);
                    ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
                } else {
                    ChunkOwnerUpdatePacket packet = new ChunkOwnerUpdatePacket(player.getUUID(), null);
                    ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
                }
            }
        }
    }
}
