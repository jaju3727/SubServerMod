package net.jaju.subservermod.subclass.skill.farmer.oven;

import net.jaju.subservermod.Subservermod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OvenScreen extends AbstractContainerScreen<OvenContainer> {

    public OvenScreen(OvenContainer container, Inventory inv, Component title) {
        super(container, inv, title);

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
        int textureWidth = 160;
        int textureHeight = 160;
        int posX = (this.width - textureWidth) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/oven.png"),
                posX, 10, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/inventory.png"),
                158, 170, 0, 0, 164, 78, 164, 78);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

}
