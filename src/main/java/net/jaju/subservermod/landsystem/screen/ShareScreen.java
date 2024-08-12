package net.jaju.subservermod.landsystem.screen;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.landsystem.LandManager;
import net.jaju.subservermod.landsystem.network.ClientPacketHandler;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.landsystem.network.packet.LandManagerMethodPacket;
import net.jaju.subservermod.landsystem.network.packet.PlayerNamePacket;
import net.jaju.subservermod.util.CustomPlainTextButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class ShareScreen extends Screen {
    private List<UUID> sharerList = new ArrayList<>();
    private final String chunkKey;
    private final Player player;
    private EditBox inputField;
    private int page = 1;
    private int maxPage = 1;
    private final int minPage = 1;
    private int sharerListSize = 0;
    private final Map<Integer, CustomPlainTextButton> buttonMap = new HashMap<>();

    public ShareScreen(Component title, Player player, String chunkKey) {
        super(title);
        this.chunkKey = chunkKey;
        this.player = player;
        MinecraftForge.EVENT_BUS.register(this);
        LinkedHashMap<String, List<UUID>> chunkSharers = ClientPacketHandler.getChunkSharers();
        this.sharerList = chunkSharers.get(chunkKey);
        if (sharerList != null) {
            sharerListSize = sharerList.size();
            maxPage = (sharerListSize - 1) / 4 + 1;
        }

    }

    @Override
    protected void init() {
        initializedWidgets();
    }

    private void initializedWidgets() {
        this.clearWidgets();

        int standardX = 70;
        int standardY = 30;
        int intervalX = 120;
        int intervalY = 60;

        if (sharerList != null) {
            int end = page == maxPage ? (sharerListSize - 1) % 4 + 1 : 4;

            for (int i = 0; i < end; i++) {
                int getI = (page - 1) * 4 + i;
                UUID sharerUUID = sharerList.get(getI);
                ModNetworking.INSTANCE.sendToServer(new PlayerNamePacket(sharerUUID));

                CustomPlainTextButton textButton = new CustomPlainTextButton(
                        standardX + 45 + (i % 2) * intervalX, // 버튼의 X 좌표
                        standardY + 100 + (i / 2) * intervalY, // 버튼의 Y 좌표
                        0,
                        0,
                        Component.literal("Player Not Found"), // 버튼 텍스트 초기값
                        button -> {},
                        minecraft.font,
                        1.5f, 0xA4A4A4
                );
                this.addRenderableWidget(new ImageButton(standardX + 40 + (i % 2) * intervalX,
                        standardY + 120 + (i / 2) * intervalY,
                        60, 18, 0, 0, 1,
                        new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/delete.png"),
                        60, 18, button -> {
                    ModNetworking.INSTANCE.sendToServer(new LandManagerMethodPacket(chunkKey, sharerList.get(getI), "removeChunkSharer"));
                    this.onClose();
                }));

                this.addRenderableWidget(textButton);
                buttonMap.put(getI, textButton);
            }
        }

        inputField = new EditBox(this.font, standardX+36, standardY + 36, 220, 45, Component.literal("Enter here"));
        inputField.setMaxLength(100); // 최대 입력 길이 설정
        inputField.setBordered(true); // 경계선 표시
        inputField.setVisible(true); // 표시 여부 설정
        inputField.setEditable(true); // 편집 가능 여부 설정
        inputField.setHint(Component.literal("플레이어의 닉네임을 입력하세요."));
        this.addRenderableWidget(inputField);

        this.addRenderableWidget(new ImageButton(standardX + 260, standardY + 38,
                40, 40, 0, 0, 0,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/allow.png"),
                40, 40, button -> {
            String playerName = inputField.getValue();
            ModNetworking.INSTANCE.sendToServer(new PlayerNamePacket(playerName));
            inputField.setValue("");
        }));

        int x = 150;
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

    private void rightPage() {
        if (page < maxPage) {
            page++;
            initializedWidgets();
        }
    }

    private void leftPage() {
        if (page > minPage) {
            page--;
            initializedWidgets();
        }
    }

    @SubscribeEvent
    public void onPlayerUUIDReceived(ClientPacketHandler.PlayerUUIDReceivedEvent event) {
        UUID receivedUUID = event.getPlayerUUID();
        if (receivedUUID != null) {
            ModNetworking.INSTANCE.sendToServer(new LandManagerMethodPacket(chunkKey, receivedUUID, "addChunkSharer"));
            player.sendSystemMessage(Component.literal("플레이어를 공유자로 추가했습니다."));
        } else {
            player.sendSystemMessage(Component.literal("플레이어 UUID를 가져오지 못했습니다."));
        }
        this.onClose();
    }

    @SubscribeEvent
    public void onPlayerNameReceived(ClientPacketHandler.PlayerNameReceivedEvent event) {
        String playerName = event.getPlayerName();
        UUID playerUUID = event.getPlayerUUID();
        for (Map.Entry<Integer, CustomPlainTextButton> entry : buttonMap.entrySet()) {
            if (sharerList.get(entry.getKey()).equals(playerUUID)) {
                entry.getValue().setMessage(Component.literal(playerName));
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        MinecraftForge.EVENT_BUS.unregister(this);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int width = 300;
        int height = (int) (300*0.833);
        int centerX = (this.width - width) / 2;
        int centerY = (this.height - height) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/landsystem_management_background.png"),
                centerX, centerY, 0, 0, width, height, width, height);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        inputField.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.inputField.isFocused()) {
            return this.inputField.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

}
