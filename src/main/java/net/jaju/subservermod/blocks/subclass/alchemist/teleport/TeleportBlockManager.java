package net.jaju.subservermod.blocks.subclass.alchemist.teleport;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TeleportBlockManager {
    private static TeleportBlockManager INSTANCE;
    private static final String FILE_PATH = "config/teleport.json";
    private final Gson gson = new Gson();
    public static HashMap<UUID, String> teleportMap = new HashMap<>();

    public TeleportBlockManager() {
        loadTeleportMap();
    }

    public static TeleportBlockManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeleportBlockManager();
        }
        return INSTANCE;
    }

    private void saveTeleportMap() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(teleportMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTeleportMap() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<Map<UUID, String>>() {}.getType();
            Map<UUID, String> loadedMailboxes = gson.fromJson(reader, type);
            if (loadedMailboxes != null) {
                teleportMap.putAll(loadedMailboxes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTeleportInfo(UUID playerUUID, String coor) {
        String preCoor = teleportMap.getOrDefault(playerUUID, "");
        if (!Objects.equals(preCoor, "")) {
            preCoor += "/";
        }
        preCoor += coor;
        teleportMap.put(playerUUID, preCoor);
        saveTeleportMap();
    }

    public void removeTeleportInfo(UUID playerUUID, String coor) {
        String preCoor = teleportMap.get(playerUUID);
        if (preCoor.split("/").length == 1) {
            teleportMap.remove(playerUUID);
        } else {
            teleportMap.put(playerUUID, preCoor.replace("/"+coor, ""));
        }
        saveTeleportMap();
    }

    public void removeTeleportInfo(String coor) {
        for (var entry: teleportMap.entrySet()) {
            String preCoor = entry.getValue();
            UUID playerUUID = entry.getKey();
            for (var forCoor: preCoor.split("/")) {
                if (Objects.equals(forCoor, coor)) {
                    if (preCoor.split("/").length == 1) {
                        teleportMap.remove(playerUUID);
                    } else {
                        teleportMap.put(playerUUID, preCoor.replace("/"+forCoor, ""));
                    }
                }
            }
        }
        saveTeleportMap();
    }

    public String findOtherCoor(UUID playerUUID, String coor) {
        String returnValue = null;

        for(var forC: teleportMap.get(playerUUID).split("/")) {
            if (!forC.equals(coor)) {
                returnValue = forC;
                break;
            }
        }

        return returnValue;
    }
}
