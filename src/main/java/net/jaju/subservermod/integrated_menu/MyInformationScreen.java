package net.jaju.subservermod.integrated_menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.integrated_menu.network.ClassDataRequestFromInformationPacket;
import net.jaju.subservermod.integrated_menu.network.CommandExecutorPacket;
import net.jaju.subservermod.subclass.BaseClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public class MyInformationScreen extends Screen {
    private final Player player;
    private Map<String, BaseClass> classHashMap = null;

    public MyInformationScreen(Player player) {
        super(Component.literal("my_information"));
        this.player = player;
        ModNetworking.INSTANCE.sendToServer(new ClassDataRequestFromInformationPacket());
    }

    public void updateClass(Map<String, BaseClass> classHashMap) {
        this.classHashMap = classHashMap;
    }

    @Override
    protected void init() {
        initializeWidgets();
    }

    private void initializeWidgets() {
        this.clearWidgets();

        int standardX = 223;
        int standardY = 58;
        int width = 80;
        int height = (int) (width * 0.292);
        this.addRenderableWidget(new ImageButton(standardX,
                standardY,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/style.png"),
                width, height, button -> {
            ModNetworking.INSTANCE.sendToServer(new CommandExecutorPacket("/칭호"));
        }));
        this.addRenderableWidget(new ImageButton(standardX,
                standardY + 41,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/stat.png"),
                width, height, button -> Minecraft.getInstance().setScreen(new StatScreen(player))));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (classHashMap == null) {
            return;
        }
        this.renderBackground(guiGraphics);
        int width = 300;
        int height = (int) (width*0.833);
        int centerX = (this.width - width) / 2;
        int centerY = (this.height - height) / 2;

        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/my_information_background.png"),
                centerX, centerY, 0, 0, width, height, width, height);
        int i = 0;
        int intervalX = 48;
        int intervalY = 37;
        for (String className: List.of("Miner", "Farmer", "Fisherman", "Woodcutter", "Chef", "Alchemist")) {
            BaseClass subClass = classHashMap.get(className);
            if (subClass != null) {
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                float scale = 1.4f;
                poseStack.scale(scale, scale, scale);
                guiGraphics.drawString(this.font, subClass.getLevel() + "차",
                        (int) ((250 + i%3 * intervalX)/scale), (int) ((181 + i/3 * intervalY)/scale), 0xA4A4A4);
                poseStack.popPose();
            } else {
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                float scale = 1.4f;
                poseStack.scale(scale, scale, scale);
                guiGraphics.drawString(this.font, "-",
                        (int) ((250 + i%3 * intervalX)/scale), (int) ((181 + i/3 * intervalY)/scale), 0xA4A4A4);
                poseStack.popPose();
            }

            i++;
        }

        AbstractClientPlayer abstractPlayer = (AbstractClientPlayer) Minecraft.getInstance().level.getPlayerByUUID(player.getUUID());
        if (abstractPlayer != null) {
            ResourceLocation skinTexture = abstractPlayer.getSkinTextureLocation();
            int playerX = 145;
            int playerY = 80;
            float scale = 4.5f;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
            poseStack.translate(playerX / scale, playerY / scale, 0);

            guiGraphics.blit(skinTexture, 0, 0, 8, 8, 8, 8, 64, 64);

            guiGraphics.blit(skinTexture, 0, 8, 20, 20, 8, 12, 64, 64);

            guiGraphics.blit(skinTexture, 0, 20, 4, 20, 4, 12, 64, 64);
            guiGraphics.blit(skinTexture, 4, 20, 4, 20, 4, 12, 64, 64);

            guiGraphics.blit(skinTexture, -4, 8, 44, 20, 4, 12, 64, 64);
            guiGraphics.blit(skinTexture, 8, 8, 44, 20, 4, 12, 64, 64);

            poseStack.popPose();
        }

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        float scale = 1.4f;
        poseStack.scale(scale, scale, scale);
        guiGraphics.drawCenteredString(this.font, player.getName(), (int) (161/scale), (int) (65/scale), 0xA4A4A4);
        poseStack.popPose();


        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
