package net.jaju.subservermod.subclass;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

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
            default:
                throw new JsonParseException("Unknown class type: " + name);
        }
    }
}