package net.jaju.subservermod.landsystem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class LandManager {
    private static LandManager INSTANCE;
    private static final LinkedHashMap<String, UUID> chunkOwners = new LinkedHashMap<>();
    private static final LinkedHashMap<String, List<UUID>> chunkSharers = new LinkedHashMap<>();
    private static final String FILE_PATH_OWNER = "config/chunk_owners.json";
    private static final String FILE_PATH_SHARER = "config/chunk_sharers.json";
    private final Gson gson = new Gson();

    public LandManager() {
        loadChunkOwners();

    }

    public static LandManager getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new LandManager();
        }
        return INSTANCE;
    }


    public LinkedHashMap<String, UUID> getChunkOwners() {
        return chunkOwners;
    }

    public LinkedHashMap<String, List<UUID>> getChunkSharers() {
        return chunkSharers;
    }

    public void removeChunkOwner(String chunkKey) {
        chunkOwners.remove(chunkKey);
        chunkSharers.remove(chunkKey);
        saveChunkOwners();
    }

    public void removeChunkSharer(String chunkKey, UUID playerUUID) {
        List<UUID> sharedWith = chunkSharers.get(chunkKey);
        if (sharedWith != null) {
            sharedWith.remove(playerUUID);
            if (sharedWith.isEmpty()) {
                chunkSharers.remove(chunkKey);
            } else {
                chunkSharers.put(chunkKey, sharedWith);
            }
            saveChunkOwners();
        }
    }

    public static boolean isOwner(String chunkKey, Player player) {
        UUID owner = getOwner(chunkKey);
        return owner != null && owner.equals(player.getUUID());
    }

    public static boolean isSharedWith(String chunkKey, Player player) {
        List<UUID> sharedWith = chunkSharers.get(chunkKey);
        return sharedWith != null && sharedWith.contains(player.getUUID());
    }

    public void setOwner(String chunkKey, UUID owner) {
        chunkOwners.put(chunkKey, owner);
        saveChunkOwners();
    }

    public void addSharer(String chunkKey, UUID playerUUID) {
        List<UUID> sharedWith = chunkSharers.get(chunkKey);
        if (sharedWith == null) {
            sharedWith = new ArrayList<>();
        }
        if (!sharedWith.contains(playerUUID)) {
            sharedWith.add(playerUUID);
        }
        chunkSharers.put(chunkKey, sharedWith);
        saveChunkOwners();
    }

    public static UUID getOwner(String chunkKey) {
        return chunkOwners.get(chunkKey);
    }

    private void saveChunkOwners() {
        try (FileWriter writer = new FileWriter(FILE_PATH_OWNER)) {
            gson.toJson(chunkOwners, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(FILE_PATH_SHARER)) {
            gson.toJson(chunkSharers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChunkOwners() {
        try (FileReader reader = new FileReader(FILE_PATH_OWNER)) {
            Type type = new TypeToken<LinkedHashMap<String, UUID>>() {}.getType();
            LinkedHashMap<String, UUID> loadedChunkOwners = gson.fromJson(reader, type);
            if (loadedChunkOwners != null) {
                chunkOwners.putAll(loadedChunkOwners);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileReader reader = new FileReader(FILE_PATH_SHARER)) {
            Type type = new TypeToken<LinkedHashMap<String, List<UUID>>>() {}.getType();
            LinkedHashMap<String, List<UUID>> loadedChunkOwners = gson.fromJson(reader, type);
            if (loadedChunkOwners != null) {
                chunkSharers.putAll(loadedChunkOwners);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getChunkKey(ResourceKey<Level> world, ChunkPos chunkPos) {
        return world.location().toString() + ":" + chunkPos.x + "," + chunkPos.z;
    }
}

