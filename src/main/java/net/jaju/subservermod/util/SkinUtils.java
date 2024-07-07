package net.jaju.subservermod.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SkinUtils {
    private static final Map<String, ResourceLocation> SKIN_CACHE = new HashMap<>();

    public static ResourceLocation getSkinForPlayer(String playerName) {
        if (SKIN_CACHE.containsKey(playerName)) {
            return SKIN_CACHE.get(playerName);
        }

        try {
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

            // Download and cache the skin texture
            ResourceLocation location = downloadSkinTexture(skinUrl);
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
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();
            String value = properties.get("value").getAsString();
            String decoded = new String(Base64.getDecoder().decode(value));
            JsonObject skinData = JsonParser.parseString(decoded).getAsJsonObject();
            return skinData.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        }
    }

    private static ResourceLocation downloadSkinTexture(String skinUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(skinUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        try (InputStream inputStream = connection.getInputStream()) {
            NativeImage image = NativeImage.read(inputStream);
            DynamicTexture dynamicTexture = new DynamicTexture(image);
            return Minecraft.getInstance().getTextureManager().register("custom_skin", dynamicTexture);
        }
    }
}
