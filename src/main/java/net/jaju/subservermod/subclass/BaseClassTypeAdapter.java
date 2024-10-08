package net.jaju.subservermod.subclass;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.reflect.Type;

public class BaseClassTypeAdapter implements JsonSerializer<BaseClass>, JsonDeserializer<BaseClass> {

    @Override
    public JsonElement serialize(BaseClass src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("level", src.getLevel());
        jsonObject.addProperty("playerName", src.getPlayerName());
        return jsonObject;
    }

    @Override
    public BaseClass deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        int level = jsonObject.get("level").getAsInt();
        String playerName = jsonObject.get("playerName").getAsString();
        switch (name) {
            case "Farmer":
                return new Farmer(level, playerName);
            case "Miner":
                return new Miner(level, playerName);
            case "Alchemist":
                return new Alchemist(level, playerName);
            case "Woodcutter":
                return new Woodcutter(level, playerName);
            case "Chef":
                return new Chef(level, playerName);
            case "Fisherman":
                return new Fisherman(level, playerName);
            default:
                throw new JsonParseException("Unknown class type: " + name);
        }
    }

    private ServerPlayer getPlayerByName(String playerName) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }
        return server.getPlayerList().getPlayerByName(playerName);
    }
}