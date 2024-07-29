package net.jaju.subservermod.subclass.skill.woodcutter.woodcuttingunion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.sound.SoundPlayer;
import net.jaju.subservermod.subclass.network.GaugeSendToEntityPacket;
import net.jaju.subservermod.subclass.network.UpdateOvenRecipePacket;
import net.jaju.subservermod.subclass.network.UpdateWoodcuttingUnionRecipePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Random;

public class WoodcuttingUnionScreen extends AbstractContainerScreen<WoodcuttingUnionContainer> {
    private int gaugeX;
    private int minItemNum;
    private boolean flag = false;
    private final WoodcuttingUnionBlockEntity blockEntity;
    private static final LinkedHashMap<String, ItemStack> recipeItems = new LinkedHashMap<>();
    private static final Random random = new Random();

    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SCALE = 1;
    private static final int ITEM_SPACING = 20;

    public WoodcuttingUnionScreen(WoodcuttingUnionContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.blockEntity = container.getBlockEntity();
        recipeItems.put("Wooden_chair", new ItemStack(ModItem.WOODEN_CHAIR.get()));
        recipeItems.put("Wooden_table", new ItemStack(ModItem.WOODEN_TABLE.get()));
        recipeItems.put("Wooden_cutting_board", new ItemStack(ModItem.WOODEN_CUTTING_BOARD.get()));
        recipeItems.put("Wooden_drawer", new ItemStack(ModItem.WOODEN_DRAWER.get()));
        recipeItems.put("Wooden_shelf", new ItemStack(ModItem.WOODEN_SHELF.get()));
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

    private void renderRecipeItems(GuiGraphics guiGraphics) {
        int i = 0;
        for (var entry : recipeItems.entrySet()) {
            ItemStack itemStack = entry.getValue();
            int xPosition = 50;
            int yPosition = 50 + i * ITEM_SPACING;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

            guiGraphics.renderItem(itemStack, 0, 0);

            poseStack.popPose();
            i++;
        }
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
                SoundPlayer.playCustomSound(Minecraft.getInstance().player, new ResourceLocation(Subservermod.MOD_ID, "woodcutter_sound"), 1.0f, 1.0f);
                gaugeX -= 10;
                ModNetworking.INSTANCE.sendToServer(new GaugeSendToEntityPacket(blockEntity.getBlockPos(), gaugeX));
            }
        }));
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/inventory.png"),
                158, 190, 0, 0, 164, 78, 164, 78);
        if (flag) {
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/woodcutter/woodcuttingunion_gauge_background.png"),
                    170 - 15, 30, 0, 0, 160, 30, 160, 30);
            int X = (int) (158 * ( (float) gaugeX / (float) (100 + (minItemNum/4) * 10)));
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/woodcutter/woodcuttingunion_gauge.png"),
                    171 - 15, 31, 0, 0, X, 28, 158, 28);
        }

        renderRecipeItems(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int i = 0;
            for (var entry : recipeItems.entrySet()) {
                int xPosition = 50;
                int yPosition = 50 + i * ITEM_SPACING;

                if (isMouseOverItem(mouseX, mouseY, xPosition, yPosition, ITEM_SIZE * ITEM_SCALE, ITEM_SIZE * ITEM_SCALE)) {
                    UpdateWoodcuttingUnionRecipePacket packet = new UpdateWoodcuttingUnionRecipePacket(blockEntity.getBlockPos(), entry.getValue());
                    ModNetworking.INSTANCE.sendToServer(packet);
                    return true;
                }
                i++;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverItem(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

}
