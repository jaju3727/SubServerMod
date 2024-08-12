package net.jaju.subservermod.player;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class PlayerManager {
    private static final String CONFIG_PATH = "config/management.json";
    private static Set<String> allowedPlayers = new HashSet<>();

    static {
        loadConfig();
    }

    public static void loadConfig() {
        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            JsonArray players = jsonObject.getAsJsonArray("allowedPlayers");

            allowedPlayers.clear();
            for (int i = 0; i < players.size(); i++) {
                allowedPlayers.add(players.get(i).getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPlayerAllowed(ServerPlayer player) {
        return allowedPlayers.contains(player.getGameProfile().getName());
    }
}