package net.jaju.subservermod.coinsystem;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.Subservermod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "subservermod", value = Dist.CLIENT)
public class CoinHud {
    private static final Map<ServerPlayer, CoinData> playerCoinDataMap = new HashMap<>();
    public static LinkedHashMap<String, Integer> coinMap = new LinkedHashMap<>();

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Font fontRenderer = mc.font;

        int x = 10;
        int y = 10;
        int intervalX = 30;
        int intervalY = 13;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        int i = 0;
        for (Map.Entry<String, Integer> coin : coinMap.entrySet()) {
            poseStack.pushPose();
            poseStack.translate(x + 4 + i / 4 * intervalX, y + i % 4 * intervalY , 0);
            if (i==0) poseStack.translate(15, 0, 0);
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/coin/" + coin.getKey() + ".png"),
                    0, 0, 0, 0, 12, 12, 12, 12);
            poseStack.popPose();
            String coinText = coin.getValue().toString();

            poseStack.pushPose();
            poseStack.translate(x + 30 + i / 4 * intervalX, y + 4 + i % 4 * intervalY , 0);
            if (i==0) poseStack.translate(15, 0, 0);
            guiGraphics.drawCenteredString(fontRenderer, coinText, 0, 0, 0xFFFFFF);
            poseStack.popPose();

            i++;
            if (i == 4) i++;
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.player;
            CoinData coinData = getCoinData(player);
            coinData.loadFromPlayer(player);
            updateCoinMap(coinData);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            CoinData coinData = getCoinData(player);
            coinData.loadFromPlayer(player);
            updateCoinMap(coinData);
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

    public static void updateCoinMap(CoinData coinData) {
        coinMap.put("subcoin", coinData.getSubcoin());
        coinMap.put("chefcoin", coinData.getChefcoin());
        coinMap.put("farmercoin", coinData.getFarmercoin());
        coinMap.put("fishermancoin", coinData.getFishermancoin());
        coinMap.put("alchemistcoin", coinData.getAlchemistcoin());
        coinMap.put("minercoin", coinData.getMinercoin());
        coinMap.put("woodcuttercoin", coinData.getWoodcuttercoin());
    }

    public static CoinData getCoinData(ServerPlayer player) {
        return playerCoinDataMap.computeIfAbsent(player, p -> new CoinData());
    }
}

