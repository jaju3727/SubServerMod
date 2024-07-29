package net.jaju.subservermod.auction;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.jaju.subservermod.util.ItemStackSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class AuctionManager {
    private static AuctionManager INSTANCE;
    private static final String FILE_PATH = "config/auction.json";
    private final Gson gson = new Gson();
    private static List<AuctionItem> auctionItems = new ArrayList<>();

    public AuctionManager() {
        loadAuctionItems();
    }

    public static AuctionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuctionManager();
        }
        return INSTANCE;
    }

    public List<AuctionItem> getAuctionItems() {
        return auctionItems;
    }

    public void addItemToAuction(ServerPlayer player, ItemStack itemStack, String coinType, int coinNum) {
        JsonObject serializedItem = ItemStackSerializer.serialize(itemStack);
        long timestamp = System.currentTimeMillis();
        String playerName = player.getName().getString();
        auctionItems.add(new AuctionItem(timestamp, playerName, serializedItem, coinType, coinNum, player.getUUID()));
        saveAuctionItems();
    }

    private void saveAuctionItems() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(auctionItems, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<AuctionItem> getAuctionItemsForPlayer(UUID playerUUID) {
        return auctionItems.stream()
                .filter(item -> item.playerUUID().equals(playerUUID))
                .collect(Collectors.toList());
    }

    public boolean removeItemFromAuction(UUID playerUUID, int index) {
        List<AuctionItem> playerItems = getAuctionItemsForPlayer(playerUUID);
        if (index >= 0 && index < playerItems.size()) {
            AuctionItem itemToRemove = playerItems.get(index);
            auctionItems.remove(itemToRemove);

            // 플레이어에게 아이템 돌려주기
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            ServerPlayer player = null;
            if (server != null) {
                player = server.getPlayerList().getPlayer(playerUUID);
            }
            if (player != null) {
                ItemStack itemStack = ItemStackSerializer.deserialize(itemToRemove.item());
                if (!player.getInventory().add(itemStack)) {
                    player.drop(itemStack, false);
                }
                player.displayClientMessage(Component.literal("경매장에서 아이템을 회수하셨습니다."), true);
            }

            return true;
        }
        return false;
    }

    public List<AuctionItem> getAllAuctionItems() {
        return new ArrayList<>(auctionItems);
    }

    private void loadAuctionItems() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<List<AuctionItem>>() {}.getType();
            List<AuctionItem> loadedAuctionItems = gson.fromJson(reader, type);
            if (loadedAuctionItems != null) {
                auctionItems.addAll(loadedAuctionItems);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateAuctionItems(List<AuctionItem> updatedItems) {
        auctionItems = updatedItems;
        saveAuctionItems();
    }
}
