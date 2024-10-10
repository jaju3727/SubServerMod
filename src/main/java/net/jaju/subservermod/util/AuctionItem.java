package net.jaju.subservermod.util;

import com.google.gson.JsonObject;

import java.util.UUID;

public record AuctionItem(long timestamp, String playerName, JsonObject item, String coinType, int coinNum,
                          UUID playerUUID) {

    @Override
    public String toString() {
        return "AuctionItem{" +
                "timestamp=" + timestamp +
                ", playerName='" + playerName + '\'' +
                ", item=" + item +
                ", coinType='" + coinType + '\'' +
                ", coinNum=" + coinNum +
                ", playerUUID=" + playerUUID +
                '}';
    }
}