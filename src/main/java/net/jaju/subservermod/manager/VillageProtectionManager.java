package net.jaju.subservermod.manager;

import net.jaju.subservermod.util.JsonUtils;
import net.jaju.subservermod.util.Village;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VillageProtectionManager {

    private static Map<String, Village> villages = new HashMap<>();
    private static Map<UUID, String> playerVillages = new HashMap<>();

    static {
        loadVillages();
    }

    public static void addVillage(String name, BlockPos pos1, BlockPos pos2, boolean protectable) {
        villages.put(name, new Village(name, pos1, pos2, protectable));
        JsonUtils.saveVillages(villages);
    }

    public static boolean isInProtectedVillage(BlockPos pos, Level level) {
        for (Village village : villages.values()) {
            if (village.isWithinBounds(pos, level)) {
                return village.getProtectable();
            }
        }
        return false;
    }

    public static boolean isInProtectedVillage2(BlockPos pos, Level level) {
        for (Village village : villages.values()) {
            if (village.isWithinBounds(pos, level)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInProtectedVillage3(BlockPos pos, Level level) {
        for (Village village : villages.values()) {
            if (village.isWithinBounds2(pos, level)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canPlayerModify(Player player, BlockPos pos) {
        if (player.hasPermissions(2)) {
            return true;
        }
        return !isInProtectedVillage(pos, player.level());
    }

    public static boolean canPlayerModify2(Player player, BlockPos pos) {
        if (player.hasPermissions(2)) {
            return true;
        }
        return !isInProtectedVillage2(pos, player.level());
    }

    public static void loadVillages() {
        Map<String, Village> loadedVillages = JsonUtils.loadVillages();
        if (loadedVillages != null) {
            villages = loadedVillages;
        }
    }

    public static String getVillageAt(BlockPos pos, Level level) {
        for (Village village : villages.values()) {
            if (village.isWithinBounds(pos, level)) {
                return village.getName();
            }
        }
        return null;
    }

    public static String getPlayerVillage(UUID playerUUID) {
        return playerVillages.get(playerUUID);
    }

    public static void updatePlayerVillage(UUID playerUUID, String villageName) {
        playerVillages.put(playerUUID, villageName);
    }

    public static void removePlayerVillage(UUID playerUUID) {
        playerVillages.remove(playerUUID);
    }
}
