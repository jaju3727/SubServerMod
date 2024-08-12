package net.jaju.subservermod.subclass.skill.miner.crafting;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.network.SetFlagPacket;
import net.jaju.subservermod.sound.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CraftingScreen extends AbstractContainerScreen<CraftingContainer> {
    private final CraftingBlockEntity blockEntity;

    public CraftingScreen(CraftingContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.blockEntity = container.getBlockEntity();

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
        int textureWidth = 190;
        int textureHeight = 190;
        int posX = (this.width - textureWidth) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/miner/crafting_background.png"),
                posX, 0, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        this.addRenderableWidget(new ImageButton(280, 119,
                30, 15, 0, 0, 1,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/miner/crafting_button.png"),
                30, 15, button -> {
            //$
//            SoundPlayer.playCustomSound(Minecraft.getInstance().player, new ResourceLocation(Subservermod.MOD_ID, "miner_sound"), 1.0f, 1.0f);
            SetFlagPacket packet = new SetFlagPacket(blockEntity.getBlockPos(), true);
            ModNetworking.INSTANCE.sendToServer(packet);
        }));

        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/inventory.png"),
                158, 190, 0, 0, 164, 78, 164, 78);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

}
