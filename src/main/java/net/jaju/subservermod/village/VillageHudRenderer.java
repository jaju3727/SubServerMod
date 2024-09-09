package net.jaju.subservermod.village;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.Subservermod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, value = Dist.CLIENT)
public class VillageHudRenderer {
    private static final Map<UUID, HudData> playerHudData = new HashMap<>();

    public static void showVillageImage(UUID playerUUID, String villageName) {
        if (!(Objects.equals(villageName, "gemshore") || Objects.equals(villageName, "perrygrass") || Objects.equals(villageName, "runegrove")
                || Objects.equals(villageName, "p" ) || Objects.equals(villageName, "m") || Objects.equals(villageName, "s")
                || Objects.equals(villageName, "dungeon1") || Objects.equals(villageName, "dungeon2")
                || Objects.equals(villageName, "dungeon3") || Objects.equals(villageName, "dungeon4"))) villageName = "else";

        playerHudData.put(playerUUID, new HudData(System.currentTimeMillis(), new ResourceLocation(Subservermod.MOD_ID, "textures/gui/village/" + villageName + ".png")));
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
        UUID playerUUID = Minecraft.getInstance().player.getUUID();
        HudData hudData = playerHudData.get(playerUUID);

        if (hudData != null) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - hudData.displayStartTime;
            if (elapsedTime > 3000) {
                playerHudData.remove(playerUUID);
                return;
            }

            int width = event.getWindow().getGuiScaledWidth();
            int imageWidth = 200;
            int imageHeight = (int) (imageWidth * 0.5625);

            float alpha;
            if (elapsedTime <= 1300) {
                alpha = Math.min(1.0f, elapsedTime / 1300.0f);
            } else {
                alpha = Math.max(0.0f, 1.0f - (elapsedTime - 1300) / 1700.0f);
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, hudData.image);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

            PoseStack poseStack = event.getGuiGraphics().pose();
            poseStack.pushPose();
            poseStack.translate((width - imageWidth) / 2.0, 10, 0);
            event.getGuiGraphics().blit(hudData.image, 0, 0, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
            poseStack.popPose();

            RenderSystem.disableBlend();
        }
    }

    private static class HudData {
        long displayStartTime;
        ResourceLocation image;

        HudData(long displayStartTime, ResourceLocation image) {
            this.displayStartTime = displayStartTime;
            this.image = image;
        }
    }
}
