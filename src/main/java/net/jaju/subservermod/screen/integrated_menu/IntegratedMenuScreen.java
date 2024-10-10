package net.jaju.subservermod.screen.integrated_menu;

import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.screen.auction.AuctionScreen;
import net.jaju.subservermod.screen.encyclopedia.EncyclopediaScreen;
import net.jaju.subservermod.network.integrated_menu.CommandExecutorPacket;
import net.jaju.subservermod.screen.landsystem.LandManagerScreen;
import net.jaju.subservermod.screen.mailbox.MailboxScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class IntegratedMenuScreen extends Screen {
    private final Player player;

    public IntegratedMenuScreen(Player player) {
        super(Component.literal("integrated_menu"));
        this.player = player;
    }

    @Override
    protected void init() {
        initializeWidgets();
    }

    private void initializeWidgets() {
        this.clearWidgets();

        int standardX = 98;
        int standardY = 42;
        int width = 90;
        int height = 90;
        int intervalX = 95;
        int intervalY = 95;
        this.addRenderableWidget(new ImageButton(standardX,
                standardY,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/invisibility.png"),
                width, height, button -> Minecraft.getInstance().setScreen(new LandManagerScreen(Component.empty(), player))));
        this.addRenderableWidget(new ImageButton(standardX + intervalX,
                standardY,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/invisibility.png"),
                width, height, button -> Minecraft.getInstance().setScreen(new MyInformationScreen(player))));
        this.addRenderableWidget(new ImageButton(standardX + intervalX*2,
                standardY,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/invisibility.png"),
                width, height, button -> Minecraft.getInstance().setScreen(new EncyclopediaScreen(player))));
        this.addRenderableWidget(new ImageButton(standardX,
                standardY + intervalY,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/invisibility.png"),
                width, height, button -> ModNetworking.INSTANCE.sendToServer(new CommandExecutorPacket("/questlist"))));
        this.addRenderableWidget(new ImageButton(standardX + intervalX,
                standardY + intervalY,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/invisibility.png"),
                width, height, button -> Minecraft.getInstance().setScreen(new MailboxScreen(player))));
        this.addRenderableWidget(new ImageButton(standardX + intervalX*2,
                standardY + intervalY,
                width, height, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/invisibility.png"),
                width, height, button -> Minecraft.getInstance().setScreen(new AuctionScreen(player))));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int width = 300;
        int height = (int) (width*0.833);
        int centerX = (this.width - width) / 2;
        int centerY = (this.height - height) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/integrated_menu/integrated_background.png"),
                centerX, centerY, 0, 0, width, height, width, height);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
