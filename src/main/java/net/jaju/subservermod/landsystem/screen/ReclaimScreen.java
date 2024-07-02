package net.jaju.subservermod.landsystem.screen;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.network.packet.LandManagerMethodPacket;
import net.jaju.subservermod.util.CustomPlainTextButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ReclaimScreen extends Screen {

    private final Player player;
    private final String chunkKey;

    public ReclaimScreen(Component title, Player player, String chunkKey) {
        super(title);
        this.player = player;
        this.chunkKey = chunkKey;
    }

    @Override
    protected void init() {
        initializedWidgets();
    }

    private void initializedWidgets() {
        int standardX = 70;
        int standardY = 30;

        this.addRenderableWidget(new CustomPlainTextButton(
                standardX+50, standardY+46,
                0,
                0,
                Component.literal("정말 회수하시겠습니까?"), // 버튼 텍스트
                button -> {

                },
                minecraft.font,
                1.5f
        ));

        this.addRenderableWidget(new ImageButton(standardX+50, standardY+70,
                30, 15, 0, 0, 0,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/allow.png"),
                30, 15, button -> {
            ModNetworking.INSTANCE.sendToServer(new LandManagerMethodPacket(chunkKey,"removeChunkOwner"));
            player.sendSystemMessage(Component.literal("청크 소유권이 성공적으로 회수되었습니다."));
            player.sendSystemMessage(Component.literal("돈이 절반 입금되었습니다."));
            this.onClose();
        }));
        this.addRenderableWidget(new ImageButton(standardX+80, standardY+70,
                30, 15, 0, 0, 0,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/cancel.png"),
                30, 15, button -> {
            player.sendSystemMessage(Component.literal("청크 소유권 회수가 취소되었습니다."));
            this.onClose();
        }));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }


}
