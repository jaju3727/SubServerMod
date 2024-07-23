package net.jaju.subservermod.encyclopedia.network;

import net.jaju.subservermod.encyclopedia.screen.EncyclopediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Mod.EventBusSubscriber
public class ClientPacketHandler {
    private static LinkedHashMap<String, Integer> encyclopedia;
    private static HashMap<String, Boolean> discoveries;
    private static LinkedHashMap<Integer, List<ItemStack>> giftList;
    private static LinkedHashMap<Integer, Boolean> giftGet;

    public static void handleEncyclopediaDataPacket(LinkedHashMap<String, Integer> receivedEncyclopedia, HashMap<String, Boolean> receivedDiscoveries, LinkedHashMap<Integer, List<ItemStack>> receivedGiftList, LinkedHashMap<Integer, Boolean> receivedGiftGet) {
        Minecraft.getInstance().execute(() -> {
            encyclopedia = receivedEncyclopedia;
            discoveries = receivedDiscoveries;
            giftList = receivedGiftList;
            giftGet = receivedGiftGet;
        });
    }

    public static LinkedHashMap<String, Integer> getEncyclopedia() {
        return encyclopedia;
    }

    public static HashMap<String, Boolean> getDiscoveries() {
        return discoveries;
    }

    public static LinkedHashMap<Integer, List<ItemStack>> getGiftList() {
        return giftList;
    }

    public static LinkedHashMap<Integer, Boolean> getGiftGet() {
        return giftGet;
    }
}
