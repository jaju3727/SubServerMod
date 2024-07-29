package net.jaju.subservermod.subclass.skill.farmer.oven;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.shopsystem.ShopItem;
import net.jaju.subservermod.subclass.network.SetFlagPacket;
import net.jaju.subservermod.subclass.network.UpdateOvenRecipePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.List;

public class OvenScreen extends AbstractContainerScreen<OvenContainer> {
    private final OvenBlockEntity blockEntity;
    private static final LinkedHashMap<String, List<ItemStack>> recipes = new LinkedHashMap<>();

    static {
        recipes.put("Croissant", List.of(
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(ModItem.BUTTER.get(), 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                new ItemStack(ModItem.CROISSANT.get(), 1)
        ));
        recipes.put("Baguette", List.of(
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                new ItemStack(ModItem.BAGUETTE.get(), 1)
        ));
        recipes.put("Chocloate", List.of(
                new ItemStack(Items.COCOA_BEANS, 1), new ItemStack(Items.COCOA_BEANS, 1), new ItemStack(Items.COCOA_BEANS, 1),
                new ItemStack(Items.SUGAR, 1), new ItemStack(Items.SUGAR, 1), new ItemStack(Items.SUGAR, 1),
                ItemStack.EMPTY, new ItemStack(ModItem.BUTTER.get(), 1), ItemStack.EMPTY,
                new ItemStack(ModItem.CHOCOLATE.get(), 1)
        ));
        recipes.put("Brownie", List.of(
                ItemStack.EMPTY, new ItemStack(Items.EGG, 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(ModItem.CHOCOLATE.get(), 1), ItemStack.EMPTY,
                new ItemStack(Items.WHEAT, 1), new ItemStack(Items.WHEAT, 1), new ItemStack(Items.WHEAT, 1),
                new ItemStack(ModItem.BROWNIE.get(), 1)
        ));
    }

    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SCALE = 1;
    private static final int ITEM_SPACING = 20;


    public OvenScreen(OvenContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntity = container.getBlockEntity();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    private void renderRecipeItems(GuiGraphics guiGraphics) {
        int i = 0;
        for (var entry : recipes.entrySet()) {
            ItemStack itemStack = entry.getValue().get(9);
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

    private void renderRecipeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int i = 0;
        for (var entry : recipes.entrySet()) {
            int xPosition = 50;
            int yPosition = 50 + i * ITEM_SPACING;

            if (isMouseOverItem(mouseX, mouseY, xPosition, yPosition, ITEM_SIZE * ITEM_SCALE, ITEM_SIZE * ITEM_SCALE)) {
                List<ItemStack> recipe = entry.getValue();
                renderRecipeGrid(guiGraphics, mouseX, mouseY, recipe);
                break;
            }
            i++;
        }
    }

    private void renderRecipeGrid(GuiGraphics guiGraphics, int mouseX, int mouseY, List<ItemStack> recipe) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(mouseX + 10, mouseY - 10, 0);

        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                ItemStack itemStack = recipe.get(index);
                if (!itemStack.isEmpty()) {
                    guiGraphics.renderItem(itemStack, col * (ITEM_SIZE + 2), row * (ITEM_SIZE + 2));
                }
                index++;
            }
        }

        poseStack.popPose();
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
        renderRecipeTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int i = 0;
            for (var entry : recipes.entrySet()) {
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
