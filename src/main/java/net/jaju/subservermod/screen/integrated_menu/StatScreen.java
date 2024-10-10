package net.jaju.subservermod.screen.integrated_menu;

import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.network.integrated_menu.TemporaryOpRequestPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class StatScreen extends Screen {
    private final Player player;

    public StatScreen(Player player) {
        super(Component.literal("StatScreen"));
        this.player = player;
    }

    @Override
    protected void init() {
        initializeWidgets();
    }

    private void initializeWidgets() {
        int standardX = 100;
        int standardY = 100;
        int width = 110;
        int height = (int) (width * 0.5);
        this.addRenderableWidget(new ImageButton(standardX,
                standardY,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/basic.png"),
                width, height, button -> ModNetworking.INSTANCE.sendToServer(new TemporaryOpRequestPacket("/mcmmogui"))));
        this.addRenderableWidget(new ImageButton(standardX + 150,
                standardY,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/fight.png"),
                width, height, button -> ModNetworking.INSTANCE.sendToServer(new TemporaryOpRequestPacket("/mmocore:stats"))));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
