package net.jaju.subservermod.subclass.skill.woodcutter.woodcuttingunion;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.network.GaugeSendToEntityPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WoodcuttingUnionScreen extends AbstractContainerScreen<WoodcuttingUnionContainer> {
    private int gaugeX;
    private int minItemNum;
    private boolean flag = false;
    private final WoodcuttingUnionBlockEntity blockEntity;

    public WoodcuttingUnionScreen(WoodcuttingUnionContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.blockEntity = container.getBlockEntity();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    public void updateVar(int gaugeX, boolean flag, int minItemNum) {
        this.gaugeX = gaugeX;
        this.minItemNum = minItemNum;
        this.flag = flag;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int textureWidth = 185;
        int textureHeight = 185;
        int posX = (this.width - textureWidth) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/woodcutter/woodcuttingunion_background.png"),
                posX, 5, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        this.addRenderableWidget(new ImageButton(160, 105,
                30, 15, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/woodcutter/woodcuttingunion_button.png"),
                30, 15, button -> {

            if (flag) {
                gaugeX -= 10;
                ModNetworking.INSTANCE.sendToServer(new GaugeSendToEntityPacket(blockEntity.getBlockPos(), gaugeX));
            }
        }));
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/inventory.png"),
                158, 190, 0, 0, 164, 78, 164, 78);
        if (flag) {
            int X = (int) (100 * ( (float) gaugeX / (float) (100 + (minItemNum/4) * 10)));
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/woodcutter/gauge.png"),
                    170, 30, 0, 0, X, 10, X, 10);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

}
