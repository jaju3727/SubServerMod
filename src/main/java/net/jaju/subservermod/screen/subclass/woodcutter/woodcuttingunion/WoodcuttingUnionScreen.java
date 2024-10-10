package net.jaju.subservermod.screen.subclass.woodcutter.woodcuttingunion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.blocks.subclass.woodcutter.woodcuttingunion.WoodcuttingUnionBlockEntity;
import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.items.ModItems;
import net.jaju.subservermod.network.subclass.GaugeSendToEntityPacket;
import net.jaju.subservermod.network.subclass.UpdateWoodcuttingUnionRecipePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class WoodcuttingUnionScreen extends AbstractContainerScreen<WoodcuttingUnionContainer> {
    private int gaugeX;
    private int minItemNum;
    private boolean flag = false;
    private final WoodcuttingUnionBlockEntity blockEntity;
    private static final LinkedHashMap<String, List<Item>> recipes = new LinkedHashMap<>();
    private static final Random random = new Random();

    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SCALE = 1;
    private static final int ITEM_SPACING = 20;

    public WoodcuttingUnionScreen(WoodcuttingUnionContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.blockEntity = container.getBlockEntity();
        recipes.put("Wooden_chair", List.of(
                Items.OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR,
                Items.OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR,
                Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB,
                Items.OAK_FENCE, Items.AIR, Items.AIR, Items.OAK_FENCE
        , ModItems.WOODEN_CHAIR.get()));
        recipes.put("Wooden_table", List.of(
                Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB,
                Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB,
                Items.OAK_FENCE, Items.AIR, Items.AIR, Items.OAK_FENCE,
                Items.OAK_FENCE, Items.AIR, Items.AIR, Items.OAK_FENCE,
                ModItems.WOODEN_TABLE.get()));
        recipes.put("Wooden_cutting_board", List.of(
                Items.AIR, Items.AIR, Items.AIR, Items.AIR,
                Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB,
                Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB,
                Items.AIR, Items.AIR, Items.AIR, Items.AIR
                , ModItems.WOODEN_CUTTING_BOARD.get()));
        recipes.put("Wooden_drawer", List.of(
                Items.AIR, Items.OAK_SLAB, Items.OAK_SLAB, Items.AIR,
                Items.AIR, Items.OAK_SLAB, Items.OAK_SLAB, Items.AIR,
                Items.AIR, Items.OAK_SLAB, Items.OAK_SLAB, Items.AIR,
                Items.AIR, Items.OAK_SLAB, Items.OAK_SLAB, Items.AIR
                , ModItems.WOODEN_DRAWER.get()));
        recipes.put("Wooden_shelf", List.of(
                Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB,
                Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB, Items.OAK_SLAB,
                Items.STICK, Items.AIR, Items.AIR, Items.STICK,
                Items.AIR, Items.AIR, Items.AIR, Items.AIR
                , ModItems.WOODEN_SHELF.get()));
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
        for (var entry : recipes.entrySet()) {
            ItemStack itemStack = new ItemStack(entry.getValue().get(entry.getValue().size() - 1));
            int xPosition = 193 + i * ITEM_SPACING;
            int yPosition = 160;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

            guiGraphics.renderItem(itemStack, 0, 0);
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/ui_slot.png"),
                    -1, -1, 0, 0, (ITEM_SIZE + 2), (ITEM_SIZE + 2), (ITEM_SIZE + 2), (ITEM_SIZE + 2));

            poseStack.popPose();
            i++;
        }
    }

    private void renderRecipeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int i = 0;
        for (var entry : recipes.entrySet()) {
            int xPosition = 193 + i * ITEM_SPACING;
            int yPosition = 160;

            if (isMouseOverItem(mouseX, mouseY, xPosition, yPosition, ITEM_SIZE * ITEM_SCALE, ITEM_SIZE * ITEM_SCALE)) {
                List<Item> recipe = entry.getValue();
                renderRecipeGrid(guiGraphics, mouseX, mouseY, recipe);
                break;
            }
            i++;
        }
    }

    private void renderRecipeGrid(GuiGraphics guiGraphics, int mouseX, int mouseY, List<Item> recipe) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(mouseX + 10, mouseY - 10, 0);
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/16_slots.png"),
                -1, -1, 0, 0, (ITEM_SIZE + 2) * 4, (ITEM_SIZE + 2) * 4, (ITEM_SIZE + 2) * 4, (ITEM_SIZE + 2) * 4);
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                ItemStack itemStack = new ItemStack(recipe.get(index));
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
//                SoundPlayer.playCustomSound(Minecraft.getInstance().player, new ResourceLocation(Subservermod.MOD_ID, "woodcutter_sound"), 1.0f, 1.0f);
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
        renderRecipeTooltip(guiGraphics, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int i = 0;
            for (var entry : recipes.entrySet()) {
                int xPosition = 193 + i * ITEM_SPACING;
                int yPosition = 160;

                if (isMouseOverItem(mouseX, mouseY, xPosition, yPosition, ITEM_SIZE * ITEM_SCALE, ITEM_SIZE * ITEM_SCALE)) {
                    UpdateWoodcuttingUnionRecipePacket packet = new UpdateWoodcuttingUnionRecipePacket(blockEntity.getBlockPos(), new ItemStack(entry.getValue().get(16)));
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
