package net.jaju.subservermod.manager.player;

import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private static final Map<String, JsonArray> normalInventories = new HashMap<>();
    private static final Map<String, JsonArray> adminInventories = new HashMap<>();
    private static final Map<String, Boolean> playerStates = new HashMap<>(); // true if using admin inventory, false if using normal inventory

    public static void saveNormalInventory(String playerName, JsonArray inventory) {
        normalInventories.put(playerName, inventory);
    }

    public static JsonArray getNormalInventory(String playerName) {
        return normalInventories.get(playerName);
    }

    public static void saveAdminInventory(String playerName, JsonArray inventory) {
        adminInventories.put(playerName, inventory);
    }

    public static JsonArray getAdminInventory(String playerName) {
        return adminInventories.get(playerName);
    }

    public static boolean hasNormalInventory(String playerName) {
        return normalInventories.containsKey(playerName);
    }

    public static boolean hasAdminInventory(String playerName) {
        return adminInventories.containsKey(playerName);
    }

    public static boolean isUsingAdminInventory(String playerName) {
        return playerStates.getOrDefault(playerName, false);
    }

    public static void setUsingAdminInventory(String playerName, boolean isUsingAdmin) {
        playerStates.put(playerName, isUsingAdmin);
    }
}
