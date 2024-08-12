package net.jaju.subservermod.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.platform.NativeImage;
import net.jaju.subservermod.Subservermod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SkinUtils {
    private static final Map<String, ResourceLocation> SKIN_CACHE = new HashMap<>();
    private static final String SKINS_DIRECTORY = "config/skins/";

    static {
        File directory = new File(SKINS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static ResourceLocation getSkinForPlayer(String playerName) {
        if (SKIN_CACHE.containsKey(playerName)) {
            return SKIN_CACHE.get(playerName);
        }

        try {
            String skinFileName = playerName.toLowerCase() + ".png";
            String skinFilePath = SKINS_DIRECTORY + skinFileName;
            if (Files.exists(Paths.get(skinFilePath))) {
                ResourceLocation location = registerSkinTexture(skinFilePath, playerName);
                SKIN_CACHE.put(playerName, location);
                return location;
            }

            // Fetch UUID for the player
            String uuid = getUUIDFromName(playerName);
            if (uuid == null) {
                return null;
            }

            // Fetch skin URL using UUID
            String skinUrl = getSkinUrlFromUUID(uuid);
            if (skinUrl == null) {
                return null;
            }

            // Download and save skin texture
            ResourceLocation location = downloadAndSaveSkinTexture(skinUrl, skinFilePath, playerName);
            SKIN_CACHE.put(playerName, location);
            return location;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getUUIDFromName(String playerName) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(true);
            JsonObject json = JsonParser.parseReader(jsonReader).getAsJsonObject();
            return json.get("id").getAsString();
        }
    }

    private static String getSkinUrlFromUUID(String uuid) throws IOException {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(true);
            JsonObject json = JsonParser.parseReader(jsonReader).getAsJsonObject();
            JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();
            String value = properties.get("value").getAsString();
            String decoded = new String(Base64.getDecoder().decode(value));
            JsonObject skinData = JsonParser.parseString(decoded).getAsJsonObject();
            return skinData.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        }
    }

    private static ResourceLocation downloadAndSaveSkinTexture(String skinUrl, String skinFilePath, String playerName) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(skinUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(skinFilePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return registerSkinTexture(skinFilePath, playerName);
        }
    }

    private static ResourceLocation registerSkinTexture(String skinFilePath, String playerName) throws IOException {
        NativeImage image = NativeImage.read(Files.newInputStream(Paths.get(skinFilePath)));
        DynamicTexture dynamicTexture = new DynamicTexture(image);
        ResourceLocation location = new ResourceLocation(Subservermod.MOD_ID, "skins/" + playerName.toLowerCase());
        Minecraft.getInstance().getTextureManager().register(location, dynamicTexture);
        return location;
    }
}