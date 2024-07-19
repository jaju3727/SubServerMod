package net.jaju.subservermod.encyclopedia.network;

import net.jaju.subservermod.encyclopedia.screen.EncyclopediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Mod.EventBusSubscriber
public class ClientPacketHandler {
    private static LinkedHashMap<String, Integer> encyclopedia;
    private static HashMap<String, Boolean> discoveries;

    public static void handleEncyclopediaDataPacket(LinkedHashMap<String, Integer> receivedEncyclopedia, HashMap<String, Boolean> receivedDiscoveries) {
        Minecraft.getInstance().execute(() -> {
            encyclopedia = receivedEncyclopedia;
            discoveries = receivedDiscoveries;
        });
    }
//    public static void handleEncyclopediaUpdate(String itemName, Boolean itemFlag, Integer itemNum) {
//        Minecraft.getInstance().execute(() -> {
//            if (ownerUUID != null) {
//                playerInChunk.put(playerUUID, ownerUUID);
//            } else {
//                playerInChunk.remove(playerUUID);
//            }
//        });
//    }

    public static LinkedHashMap<String, Integer> getEncyclopedia() {
        return encyclopedia;
    }

    public static HashMap<String, Boolean> getDiscoveries() {
        return discoveries;
    }
}
