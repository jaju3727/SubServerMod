package net.jaju.subservermod.screen.subclass.alchemist.brewing;

import net.jaju.subservermod.Subservermod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BrewingScreen extends AbstractContainerScreen<BrewingContainer> {
    private int blazeCount;
    private int waterCount;
    private boolean brewingFlag;
    private int brewingNum = 0;

    public BrewingScreen(BrewingContainer container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    public void updateVar(int blazeCount, int waterCount, int brewingNum) {
        this.blazeCount = blazeCount;
        this.waterCount = waterCount;
        this.brewingNum = brewingNum;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int textureWidth = 170;
        int textureHeight = 116;
        int posX = (this.width - textureWidth) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/alchemist/brewing_background.png"),
                posX, 50, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/inventory.png"),
                158, 170, 0, 0, 164, 78, 164, 78);
        guiGraphics.drawCenteredString(this.font, Component.literal(String.valueOf(blazeCount)).withStyle(ChatFormatting.YELLOW), 165, 70, 0);
        guiGraphics.drawCenteredString(this.font, Component.literal(String.valueOf(waterCount)).withStyle(ChatFormatting.BLUE), 165, 88, 0);
        textureWidth = 7;
        textureHeight = 27;
        int i = brewingNum/5 % 27;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/alchemist/brewing_gauge.png"),
                    195, 107, 0, 0, textureWidth, i, textureWidth, textureHeight);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

}
