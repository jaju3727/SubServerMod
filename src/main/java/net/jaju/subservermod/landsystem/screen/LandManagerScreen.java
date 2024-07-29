package net.jaju.subservermod.landsystem.screen;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.landsystem.network.ClientPacketHandler;
import net.jaju.subservermod.util.CustomPlainTextButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.List;

public class LandManagerScreen extends Screen {
    private final List<String> ownerList = new ArrayList<>();
    private final Player player;
    private int page = 1;
    private final int maxPage;
    private final int minPage = 1;

    public LandManagerScreen(Component title, Player player) {
        super(title);
        this.player = player;
        UUID playerUUID = player.getUUID();
        LinkedHashMap<String, UUID> chunkOwners = ClientPacketHandler.getChunkOwners();
        for (Map.Entry<String, UUID> entry : chunkOwners.entrySet()) {
            if (entry.getValue().equals(playerUUID)) {
                ownerList.add(entry.getKey());
            }
        }
        maxPage = (ownerList.size() - 1) / 4 + 1;
    }

    @Override
    protected void init() {
        initializeWidgets();
    }

    private void initializeWidgets() {
        this.clearWidgets();

        int standardX = 115;
        int standardY = 80;
        int intervalX = 140;
        int intervalY = 90;

        int end = page == maxPage ? (ownerList.size() - 1) % 4 + 1 : 4;

        for (int i = 0; i < end; i++) {

            String chunkKey = ownerList.get((page - 1) * 4 + i);
            this.addRenderableWidget(new ImageButton(standardX + 50 + (i % 2) * intervalX +20,
                    standardY + 30 + (i / 2) * intervalY - 10,
                    60, 18, 0, 0, 1,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/share.png"),
                    60, 18, button -> Minecraft.getInstance().setScreen(new ShareScreen(Component.literal("Reclaim Screen"), player, chunkKey))));

            this.addRenderableWidget(new ImageButton(standardX + (i % 2) * intervalX,
                    standardY + 30 + (i / 2) * intervalY - 10,
                    60, 18, 0, 0, 1,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/reclaim.png"),
                    60, 18, button -> Minecraft.getInstance().setScreen(new ReclaimScreen(Component.literal("Reclaim Screen"), player, chunkKey))));

            this.addRenderableWidget(new CustomPlainTextButton(
                    standardX + (i % 2) * intervalX, // 버튼의 X 좌표
                    standardY + (i / 2) * intervalY, // 버튼의 Y 좌표
                    0,
                    0,
                    Component.literal(centerCoor(chunkKey)), // 버튼 텍스트
                    button -> {}, // 클릭 시 실행될 함수
                    minecraft.font,
                    1.5f
            ));
        }

        int x = 160;
        if (page != minPage) {
            this.addRenderableWidget(new ImageButton(this.width/2 - x - 30, (this.height)/2,
                    30, 15, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/leftarrow.png"),
                    30, 15, button -> leftPage()));
        }
        if (page != maxPage) {
            this.addRenderableWidget(new ImageButton(this.width/2 + x, (this.height)/2,
                    30, 15, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/rightarrow.png"),
                    30, 15, button -> rightPage()));
        }
    }


    private String centerCoor(String chunkKey) {
        List<String> co = Arrays.stream(chunkKey.split(":")).toList();
        List<String> coor = List.of(co.get(co.size() - 1).split(","));
        int x = Integer.parseInt(coor.get(0));
        int z = Integer.parseInt(coor.get(1));
        String centerX = String.valueOf(x * 16 + 8);
        String centerZ = String.valueOf(z * 16 + 8);

        return "중심좌표:" + centerX + "," + centerZ;
    }

    private void rightPage() {
        if (page < maxPage) {
            page++;
            initializeWidgets();
        }
    }

    private void leftPage() {
        if (page > minPage) {
            page--;
            initializeWidgets();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int width = 300;
        int height = (int) (300*0.833);
        int centerX = (this.width - width) / 2;
        int centerY = (this.height - height) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/landsystem_background.png"),
                centerX, centerY, 0, 0, width, height, width, height);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
