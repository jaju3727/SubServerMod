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
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.bukkit.Server;

import java.util.*;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, value = Dist.CLIENT)
public class CoinHud {
    private static final Map<UUID, CoinData> playerCoinDataMap = new HashMap<>();
    public static final Capability<CoinData> COIN_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(CoinData.class);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Player> event) {
        event.addCapability(new ResourceLocation(Subservermod.MOD_ID, "coin_data"), new CoinDataProvider());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(COIN_CAPABILITY).ifPresent(oldStore -> {
            event.getEntity().getCapability(COIN_CAPABILITY).ifPresent(newStore -> {
                newStore.deserializeNBT(oldStore.serializeNBT());
            });
        });
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        CoinData coinData = playerCoinDataMap.get(mc.player.getUUID());
        if (coinData == null) return;

        Font fontRenderer = mc.font;
        int x = 10;
        int y = 10;
        int intervalX = 30;
        int intervalY = 8;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();

        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/coin/coin_ui.png"),
                -40, -10, 0, 0, 180, 101, 180, 101);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        int i = 0;
        renderCoin(guiGraphics, poseStack, fontRenderer, "subcoin", coinData.getSubcoin(), x, y, intervalX, intervalY, i);
        i++;
        renderCoin(guiGraphics, poseStack, fontRenderer, "chefcoin", coinData.getChefcoin(), x, y, intervalX, intervalY, i);
        i++;
        renderCoin(guiGraphics, poseStack, fontRenderer, "farmercoin", coinData.getFarmercoin(), x, y, intervalX, intervalY, i);
        i++;
        renderCoin(guiGraphics, poseStack, fontRenderer, "fishermancoin", coinData.getFishermancoin(), x, y, intervalX, intervalY, i);
        i++;
        i++;
        renderCoin(guiGraphics, poseStack, fontRenderer, "alchemistcoin", coinData.getAlchemistcoin(), x, y, intervalX, intervalY, i);
        i++;
        renderCoin(guiGraphics, poseStack, fontRenderer, "minercoin", coinData.getMinercoin(), x, y, intervalX, intervalY, i);
        i++;
        renderCoin(guiGraphics, poseStack, fontRenderer, "woodcuttercoin", coinData.getWoodcuttercoin(), x, y, intervalX, intervalY, i);
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
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            CoinData coinData = getCoinData(player);
            coinData.loadFromPlayer(player);
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

    public static void updateCoinData(UUID playerUUID, CoinData coinData) {
        playerCoinDataMap.put(playerUUID, coinData);
    }

    public static CoinData getCoinData(ServerPlayer player) {
        return playerCoinDataMap.computeIfAbsent(player.getUUID(), p -> new CoinData());
    }
}

