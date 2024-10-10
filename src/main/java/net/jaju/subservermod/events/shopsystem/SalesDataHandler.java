package net.jaju.subservermod.events.shopsystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.jaju.subservermod.Subservermod;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class SalesDataHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String SALES_DATA_PATH = "config/sales_data.json";
    private static final String BACKUP_PATH = "config/backup_sales_data.json";
    private static final Map<String, Map<String, Map<String, Integer>>> salesData = new HashMap<>();
    private static final Map<String, Map<String, String>> topPlayers = new HashMap<>();

    public static void recordSale(String playerUUID, String coinType, int amount) {
        String date = LocalDate.now().toString();
        salesData.computeIfAbsent(date, k -> new HashMap<>())
                .computeIfAbsent(coinType, k -> new HashMap<>())
                .merge(playerUUID, amount, Integer::sum);
        saveSalesData();
    }

    private static void saveSalesData() {
        try (FileWriter writer = new FileWriter(SALES_DATA_PATH)) {
            GSON.toJson(salesData, writer);
        } catch (IOException e) {
            Subservermod.LOGGER.error("Failed to save sales data", e);
        }
    }

    private static void saveTopPlayers() {
        try (FileWriter writer = new FileWriter(BACKUP_PATH)) {
            GSON.toJson(topPlayers, writer);
        } catch (IOException e) {
            Subservermod.LOGGER.error("Failed to save top players data", e);
        }
    }

    public static void loadSalesData() {
        Path salesDataPath = Paths.get(SALES_DATA_PATH);
        if (Files.exists(salesDataPath)) {
            try {
                String json = new String(Files.readAllBytes(salesDataPath));
                Type type = new TypeToken<Map<String, Map<String, Map<String, Integer>>>>() {}.getType();
                Map<String, Map<String, Map<String, Integer>>> loadedData = GSON.fromJson(json, type);
                salesData.clear();
                salesData.putAll(loadedData);
            } catch (IOException e) {
                Subservermod.LOGGER.error("Failed to load sales data", e);
            }
        }
    }

    public static void loadTopPlayersData() {
        Path backupPath = Paths.get(BACKUP_PATH);
        if (Files.exists(backupPath)) {
            try {
                String json = new String(Files.readAllBytes(backupPath));
                Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
                Map<String, Map<String, String>> loadedData = GSON.fromJson(json, type);
                topPlayers.clear();
                topPlayers.putAll(loadedData);
            } catch (IOException e) {
                Subservermod.LOGGER.error("Failed to load top players data", e);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof FakePlayer)) {
            loadSalesData();
        }
    }

    public static void resetDailyData() {
        // 전날의 데이터를 백업합니다.
        backupSalesData();
        // salesData를 초기화합니다.
        salesData.clear();
        saveSalesData();
    }

    private static void backupSalesData() {
        String date = LocalDate.now().minusDays(1).toString();
        if (!salesData.containsKey(date)) {
            return;
        }
        Map<String, Map<String, Integer>> dailyData = salesData.get(date);
        Map<String, String> dailyTopPlayers = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> coinEntry : dailyData.entrySet()) {
            String coinType = coinEntry.getKey();
            Map<String, Integer> playerSales = coinEntry.getValue();
            String topPlayer = null;
            int maxAmount = 0;

            for (Map.Entry<String, Integer> playerEntry : playerSales.entrySet()) {
                String playerUUID = playerEntry.getKey();
                int amount = playerEntry.getValue();
                if (amount > maxAmount) {
                    maxAmount = amount;
                    topPlayer = playerUUID;
                }
            }

            if (topPlayer != null) {
                dailyTopPlayers.put(coinType, topPlayer);
            }
        }

        topPlayers.put(date, dailyTopPlayers);
        saveTopPlayers();
    }

    public static Map<String, String> getTopPlayers(String date) {
        loadTopPlayersData();
        return topPlayers.getOrDefault(date, new HashMap<>());
    }
}
