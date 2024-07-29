package net.jaju.subservermod.subclass.skill.farmer.middle_oven;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.subclass.network.UpdateOvenRecipePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;

public class MiddleOvenScreen extends AbstractContainerScreen<MiddleOvenContainer> {
    private final MiddleOvenBlockEntity blockEntity;
    private static final LinkedHashMap<String, ItemStack> recipeItems = new LinkedHashMap<>();

    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SCALE = 1;
    private static final int ITEM_SPACING = 20;


    public MiddleOvenScreen(MiddleOvenContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntity = container.getBlockEntity();
        recipeItems.put("Croissant", new ItemStack(ModItem.CROISSANT.get()));
        recipeItems.put("Baguette", new ItemStack(ModItem.BAGUETTE.get()));
        recipeItems.put("Chocolate", new ItemStack(ModItem.CHOCOLATE.get()));
        recipeItems.put("Brownie", new ItemStack(ModItem.BROWNIE.get()));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

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
        int textureWidth = 160;
        int textureHeight = 160;
        int posX = (this.width - textureWidth) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/oven.png"),
                posX, 10, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/inventory.png"),
                158, 170, 0, 0, 164, 78, 164, 78);
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
                    UpdateOvenRecipePacket packet = new UpdateOvenRecipePacket(blockEntity.getBlockPos(), entry.getKey());
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
