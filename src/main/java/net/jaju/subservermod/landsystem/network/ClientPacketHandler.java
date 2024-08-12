package net.jaju.subservermod.landsystem.network;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class ClientPacketHandler {
    private static LinkedHashMap<String, UUID> chunkOwners;
    private static LinkedHashMap<String, List<UUID>> chunkSharers;
    private static final Map<UUID, UUID> playerInChunk = new HashMap<>();
    private static UUID receivedPlayerUUID;
    private static final Map<UUID, String> playerNames = new HashMap<>();
    private static String ownerName;

    public static void handleChunkOwnersPacket(LinkedHashMap<String, UUID> receivedChunkOwners) {
        Minecraft.getInstance().execute(() -> {
            chunkOwners = receivedChunkOwners;
        });

    }
    public static void handleChunkSharersPacket(LinkedHashMap<String, List<UUID>> receivedChunkSharers) {
        Minecraft.getInstance().execute(() -> {
            chunkSharers = receivedChunkSharers;
        });
    }

    public static void handleChunkOwnerUpdatePacket(UUID playerUUID, UUID ownerUUID, String receivedOwnerName) {
        Minecraft.getInstance().execute(() -> {
            if (ownerUUID != null) {
                playerInChunk.put(playerUUID, ownerUUID);
                ownerName = receivedOwnerName;
            } else {
                playerInChunk.remove(playerUUID);
            }
        });
    }

    public static String getOwnerName() {
        return ownerName;
    }

    public static void playerNamePacket(UUID playerUUID) {
        Minecraft.getInstance().execute(() -> {
            receivedPlayerUUID = playerUUID;
            MinecraftForge.EVENT_BUS.post(new PlayerUUIDReceivedEvent(receivedPlayerUUID));
        });
    }

    public static void handlePlayerNameResponse(UUID playerUUID, String playerName) {
        Minecraft.getInstance().execute(() -> {
            playerNames.put(playerUUID, playerName);
            MinecraftForge.EVENT_BUS.post(new PlayerNameReceivedEvent(playerUUID, playerName));
        });
    }

    public static LinkedHashMap<String, UUID> getChunkOwners() {
        return chunkOwners;
    }

    public static LinkedHashMap<String, List<UUID>> getChunkSharers() {
        return chunkSharers;
    }

    public static Map<UUID, UUID> getPlayerInChunk() {
        return new HashMap<>(playerInChunk);
    }

    public static class PlayerNameReceivedEvent extends Event {
        private final UUID playerUUID;
        private final String playerName;

        public PlayerNameReceivedEvent(UUID playerUUID, String playerName) {
            this.playerUUID = playerUUID;
            this.playerName = playerName;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }

        public String getPlayerName() {
            return playerName;
        }
    }

    public static class PlayerUUIDReceivedEvent extends Event {
        private final UUID playerUUID;

        public PlayerUUIDReceivedEvent(UUID playerUUID) {
            this.playerUUID = playerUUID;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }
    }
}
