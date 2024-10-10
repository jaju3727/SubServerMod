package net.jaju.subservermod.manager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.jaju.subservermod.util.AuctionItem;
import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.network.shopsystem.UpdateInventoryPacket;
import net.jaju.subservermod.util.ItemStackSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
        Subservermod.LOGGER.info("AuctionManagerLoading");
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

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);
                if (player != null) {
                    ItemStack itemStack = ItemStackSerializer.deserialize(itemToRemove.item());
                    if (addItemsToInventory(player, itemStack) >= itemStack.getCount()) {

                        player.displayClientMessage(Component.literal("경매장에서 아이템을 회수하셨습니다."), true);
                        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                            ItemStack stack = player.getInventory().getItem(i);
                            ModNetworking.INSTANCE.sendToServer(new UpdateInventoryPacket(i, stack));
                        }

                    } else {
                        player.sendSystemMessage(Component.literal("인벤토리 공간이 충분하지 않습니다."));
                        return false;
                    }
                } else {
                    // 플레이어가 null인 경우 로그를 남기기
                    System.err.println("Error: Player not found with UUID: " + playerUUID);
                }
            } else {
                // 서버가 null인 경우 로그를 남기기
                System.err.println("Error: Server is not initialized or available.");
            }

            return true;
        }
        return false;
    }

    private int addItemsToInventory(Player player, ItemStack itemStack) {
        int remainingCount = itemStack.getCount();
        int totalAdded = 0;
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (stack.isEmpty()) {
                int addCount = Math.min(remainingCount, itemStack.getMaxStackSize());
                ItemStack newStack = itemStack.copy();
                newStack.setCount(addCount);
                player.getInventory().items.set(i, newStack);
                totalAdded += addCount;
                remainingCount -= addCount;
                if (remainingCount <= 0) {
                    break;
                }
            } else if (ItemStack.isSameItemSameTags(stack, itemStack) && stack.getCount() < stack.getMaxStackSize()) {
                int addCount = Math.min(remainingCount, stack.getMaxStackSize() - stack.getCount());
                stack.grow(addCount);
                totalAdded += addCount;
                remainingCount -= addCount;
                if (remainingCount <= 0) {
                    break;
                }
            }
        }
        return totalAdded;
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
