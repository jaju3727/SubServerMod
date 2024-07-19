package net.jaju.subservermod.subclass.skill.woodcutter.woodcutterinventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.Subservermod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class WoodcutterScreen extends InventoryScreen {
    private final Player player;
    private static final int EXTRA_SLOTS = 9;
    private final ItemStack[] extraInventory = new ItemStack[EXTRA_SLOTS];

    public WoodcutterScreen(Player player){
        super(player);
        this.player = player;

        // Initialize extra inventory slots to empty
        Arrays.fill(extraInventory, ItemStack.EMPTY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        super.renderBg(guiGraphics, partialTicks, x, y);

        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        // Render additional slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/inventorySlot"), relX + 62 + j * 18, relY + 17 + i * 18, 7, 83, 18, 18);
                ItemStack stack = extraInventory[i * 3 + j];
                if (!stack.isEmpty()) {
                    guiGraphics.renderItem(stack, relX + 62 + j * 18, relY + 17 + i * 18);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        // Check if additional slots are clicked
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                int slotX = relX + 62 + j * 18;
                int slotY = relY + 17 + i * 18;
                if (mouseX >= slotX && mouseX < slotX + 18 && mouseY >= slotY && mouseY < slotY + 18) {
                    handleSlotClick(i * 3 + j);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleSlotClick(int slotIndex) {
        ItemStack cursorStack = this.minecraft.player.containerMenu.getCarried();
        ItemStack slotStack = extraInventory[slotIndex];

        if (slotStack.isEmpty()) {
            extraInventory[slotIndex] = cursorStack.copy();
            this.minecraft.player.containerMenu.setCarried(ItemStack.EMPTY);
        } else if (cursorStack.isEmpty()) {
            this.minecraft.player.containerMenu.setCarried(slotStack.copy());
            extraInventory[slotIndex] = ItemStack.EMPTY;
        } else {
            extraInventory[slotIndex] = cursorStack.copy();
            this.minecraft.player.containerMenu.setCarried(slotStack);
        }
    }
}
