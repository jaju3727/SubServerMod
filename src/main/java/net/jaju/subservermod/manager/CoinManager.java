package net.jaju.subservermod.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.network.coinsystem.CoinDataSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, value = Dist.CLIENT)
public class CoinManager {
    private static final Map<UUID, CoinData> playerCoinDataMap = new HashMap<>();
    private static final Gson GSON = new Gson();
    private static final String DATA_FILE_PATH = "config/coindata.json";
    private static final File DATA_FILE = new File(DATA_FILE_PATH);

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        CoinData coinData = playerCoinDataMap.get(mc.player.getUUID());
        if (coinData == null) return;

        Font fontRenderer = mc.font;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        int i = 0;
        for (String type: List.of("sub_coin", "farmer_coin", "chef_coin", "fisherman_coin", "alchemist_coin", "miner_coin", "woodcutter_coin")) {
            int posX = 2;
            int posY = 2;
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/item/" + type + ".png"),
                    posX, posY + i * 11, 0, 0, 10, 10, 10, 10);

            poseStack.pushPose();
            float scale = 0.8f;
            poseStack.scale(scale, scale, scale);
            poseStack.translate((posX + 13)/scale, (posY + i * 11)/scale, 0);
            guiGraphics.drawString(fontRenderer, String.valueOf(switch (type) {
                case "sub_coin" -> coinData.getSubcoin();
                case "chef_coin" -> coinData.getChefcoin();
                case "farmer_coin" -> coinData.getFarmercoin();
                case "fisherman_coin" -> coinData.getFishermancoin();
                case "alchemist_coin" -> coinData.getAlchemistcoin();
                case "miner_coin" -> coinData.getMinercoin();
                case "woodcutter_coin" -> coinData.getWoodcuttercoin();
                default -> throw new IllegalStateException("Unexpected value: " + type);
            }), 0, 0, 0xA4A4A4);
            poseStack.popPose();
            i++;
        }
    }

    private static void renderCoin(GuiGraphics guiGraphics, PoseStack poseStack, Font fontRenderer, String coinType, int amount, int x, int y, int intervalX, int intervalY, int i) {
        poseStack.pushPose();
        poseStack.translate(x + 4 + i / 4 * intervalX, y + i % 4 * intervalY , 0);
        if (i == 0) poseStack.translate(15, 0, 0);
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/coin/" + coinType + ".png"),
                0, 0, 0, 0, 7, 7, 7, 7);
        poseStack.popPose();

        String coinText = String.valueOf(amount);
        poseStack.pushPose();
        float scale = 0.6f;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(x + 30 + i / 4 * intervalX, y + 4 + i % 4 * intervalY , 0);
        if (i == 0) poseStack.translate(15, 0, 0);
        guiGraphics.drawString(fontRenderer, coinText, 0, 0, 0xFFFFFF);
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.player;
            CoinData coinData = getCoinData(player);
            coinData.loadFromPlayer(player);
            ModNetworking.sendToPlayer(player, new CoinDataSyncPacket(player.getUUID(), coinData));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            CoinData coinData = getCoinData(player);
            coinData.loadFromPlayer(player);
            ModNetworking.sendToPlayer(player, new CoinDataSyncPacket(player.getUUID(), coinData));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            CoinData coinData = getCoinData(player);
            coinData.saveToPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer original = (ServerPlayer) event.getOriginal();
            ServerPlayer clone = (ServerPlayer) event.getEntity();
            CoinData originalCoinData = getCoinData(original);
            CoinData cloneCoinData = getCoinData(clone);
            cloneCoinData.deserializeNBT(originalCoinData.serializeNBT());
            cloneCoinData.saveToPlayer(clone);
            ModNetworking.sendToPlayer(clone, new CoinDataSyncPacket(clone.getUUID(), cloneCoinData));
        }
    }

    public static void updateCoinData(UUID playerUUID, CoinData coinData) {
        playerCoinDataMap.put(playerUUID, coinData);
    }

    public static CoinData getCoinData(ServerPlayer player) {
        return playerCoinDataMap.computeIfAbsent(player.getUUID(), p -> new CoinData());
    }

    public static CoinData getCoinData(UUID playerUUID) {
        return playerCoinDataMap.computeIfAbsent(playerUUID, p -> new CoinData());
    }

    public static void saveCoinData() {
        try (FileWriter writer = new FileWriter(DATA_FILE_PATH)) {
            GSON.toJson(playerCoinDataMap, writer);
        } catch (IOException e) {
            Subservermod.LOGGER.error("Failed to save coin data", e);
        }
    }

    public static void loadCoinData() {
        if (DATA_FILE.exists()) {
            try (FileReader reader = new FileReader(DATA_FILE_PATH)) {
                Type type = new TypeToken<Map<UUID, CoinData>>(){}.getType();
                Map<UUID, CoinData> data = GSON.fromJson(reader, type);
                if (data != null) {
                    playerCoinDataMap.clear();
                    playerCoinDataMap.putAll(data);
                }
            } catch (IOException e) {
                Subservermod.LOGGER.error("Failed to load coin data", e);
            }
        }
    }

    public static Map<String, List<Map.Entry<UUID, Integer>>> getTopPlayers() {
        Map<String, List<Map.Entry<UUID, Integer>>> topPlayers = new HashMap<>();
        Map<UUID, CoinData> playerData = new HashMap<>(playerCoinDataMap);

        for (String coinType : Arrays.asList("subcoin", "chefcoin", "farmercoin", "fishermancoin", "alchemistcoin", "minercoin", "woodcuttercoin")) {
            List<Map.Entry<UUID, Integer>> sortedList = playerData.entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey(), getCoinAmount(entry.getValue(), coinType)))
                    .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                    .limit(5)
                    .collect(Collectors.toList());
            topPlayers.put(coinType, sortedList);
        }

        return topPlayers;
    }

    private static int getCoinAmount(CoinData coinData, String coinType) {
        return switch (coinType) {
            case "subcoin" -> coinData.getSubcoin();
            case "chefcoin" -> coinData.getChefcoin();
            case "farmercoin" -> coinData.getFarmercoin();
            case "fishermancoin" -> coinData.getFishermancoin();
            case "alchemistcoin" -> coinData.getAlchemistcoin();
            case "minercoin" -> coinData.getMinercoin();
            case "woodcuttercoin" -> coinData.getWoodcuttercoin();
            default -> 0;
        };
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        loadCoinData();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        saveCoinData();
    }
}

