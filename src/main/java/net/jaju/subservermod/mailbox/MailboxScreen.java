package net.jaju.subservermod.mailbox;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.mailbox.network.ClientPacketHandler;
import net.jaju.subservermod.mailbox.network.packet.UpdateMailboxPacket;
import net.jaju.subservermod.shopsystem.network.UpdateInventoryPacket;
import net.jaju.subservermod.util.ItemStackSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MailboxScreen extends Screen {
    private final Player player;
    private final List<ItemStack> mailboxItems;
    private final int standardX = 117;
    private final int standardY = 68;
    private final int intervalX = 31;
    private final int intervalY = 30;
    private int page = 1;
    private int maxPage;
    private final int minPage = 1;
    private static final int numPerPage = 54;

    public MailboxScreen(Player player) {
        super(Component.literal("Mailbox"));
        this.player = player;
        this.mailboxItems = new ArrayList<>();
        List<JsonObject> serializedItems = ClientPacketHandler.getMailboxes().get(player.getUUID());
        if (serializedItems != null) {
            for (JsonObject serializedItem : serializedItems) {
                mailboxItems.add(ItemStackSerializer.deserialize(serializedItem));
            }
        }
        this.maxPage = (mailboxItems.size() - 1) / numPerPage + 1;
    }

    @Override
    protected void init() {
        super.init();
    }

    private void takeButtonClick(ItemStack mailboxItem) {
        int takeCount = mailboxItem.getCount();
        ItemStack itemStack = mailboxItem.copy();
        itemStack.setCount(takeCount);

        int takenAmount = addItemsToInventory(player, itemStack);
        if (takenAmount > 0) {
            mailboxItem.shrink(takenAmount);
            if (mailboxItem.isEmpty()) {
                mailboxItems.remove(mailboxItem);
            }

            List<JsonObject> updatedMailboxItems = new ArrayList<>();
            for (ItemStack stack : mailboxItems) {
                updatedMailboxItems.add(ItemStackSerializer.serialize(stack));
            }

            ModNetworking.INSTANCE.sendToServer(new UpdateMailboxPacket(player.getUUID(), updatedMailboxItems));

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                ModNetworking.INSTANCE.sendToServer(new UpdateInventoryPacket(i, stack));
            }

        } else {
            player.sendSystemMessage(Component.literal("인벤토리 공간이 충분하지 않습니다."));
        }
    }

    private int addItemsToInventory(Player player, ItemStack itemStack) {
        int remainingCount = itemStack.getCount();
        int totalAdded = 0;
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (stack.isEmpty()) {
                int addCount = Math.min(remainingCount, itemStack.getMaxStackSize());
                ItemStack newStack = itemStack.copy();
                newStack.setCount(addCount);
                player.getInventory().items.set(i, newStack);
                totalAdded += addCount;
                remainingCount -= addCount;
                if (remainingCount <= 0) {
                    break;
                }
            } else if (ItemStack.isSameItemSameTags(stack, itemStack) && stack.getCount() < stack.getMaxStackSize()) {
                int addCount = Math.min(remainingCount, stack.getMaxStackSize() - stack.getCount());
                stack.grow(addCount);
                totalAdded += addCount;
                remainingCount -= addCount;
                if (remainingCount <= 0) {
                    break;
                }
            }
        }
        return totalAdded;
    }

    private void rightPage() {
        if (page < maxPage) {
            page++;
        }
    }

    private void leftPage() {
        if (page > minPage) {
            page--;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left mouse button
            float scale = 1.4f;
            int end = page == maxPage ? (mailboxItems.size() - 1) % numPerPage + 1 : numPerPage;
            int start = numPerPage * (page - 1);
            end += start;
            for (int i = start; i < end; i++) {
                ItemStack mailboxItem = mailboxItems.get(i);
                int restI = i % numPerPage;
                int xPosition = standardX + restI % 9 * intervalX - 10 - 2;
                int yPosition = standardY + restI / 9 * intervalY;
                int scaledMouseX = (int) ((mouseX - xPosition) / scale);
                int scaledMouseY = (int) ((mouseY - yPosition) / scale);

                if (scaledMouseX >= 0 && scaledMouseX <= 16 && scaledMouseY >= 0 && scaledMouseY <= 16) {
                    takeButtonClick(mailboxItem);
                    return true;
                }
            }
        }

        int x = 175;
        int rightArrowX = this.width / 2 + x - 30;
        int leftArrowX = this.width / 2 - x;
        int arrowY = this.height / 2;

        if (button == 0) {
            if (mouseX >= rightArrowX && mouseX <= rightArrowX + 30 && mouseY >= arrowY && mouseY <= arrowY + 15) {
                if (page != maxPage) rightPage();
                return true;
            } else if (mouseX >= leftArrowX && mouseX <= leftArrowX + 30 && mouseY >= arrowY && mouseY <= arrowY + 15) {
                if (page != minPage) leftPage();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int textureWidth = 300;
        int textureHeight = (int) (textureWidth * 0.8333);
        int posX = (this.width - textureWidth) / 2;
        int posY = (this.height - textureHeight) / 2;
        int x = 175;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/mailbox/mailboxbackground.png"),
                posX, posY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        if (page != minPage) {
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/leftarrow.png"),
                    this.width / 2 - x, this.height / 2, 0, 0, 30, 15, 30, 15);
        }
        if (page != maxPage) {
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/rightarrow.png"),
                    this.width / 2 + x - 30, this.height / 2, 0, 0, 30, 15, 30, 15);
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderMailboxItems(guiGraphics);
        renderTooltipsForItems(guiGraphics, mouseX, mouseY);
    }

    private void renderMailboxItems(GuiGraphics guiGraphics) {
        float scale = 1.4f;
        int end = page == maxPage ? (mailboxItems.size() - 1) % numPerPage + 1 : numPerPage;
        int start = numPerPage * (page - 1);
        end += start;
        for (int i = start; i < end; i++) {
            ItemStack mailboxItem = mailboxItems.get(i);
            int restI = i % numPerPage;
            int xPosition = standardX + restI % 9 * intervalX - 10 - 2;
            int yPosition = standardY + restI / 9 * intervalY;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(scale, scale, scale);

            guiGraphics.renderItem(mailboxItem, 0, 0);

            poseStack.translate(10, 10, 200);
            String countLabel = String.valueOf(mailboxItem.getCount());
            guiGraphics.drawString(Minecraft.getInstance().font, countLabel, 0, 0, 0xFFFFFF);

            poseStack.popPose();
        }
    }

    private void renderTooltipsForItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        float scale = 1.4f;

        int end = page == maxPage ? (mailboxItems.size() - 1) % numPerPage + 1 : numPerPage;
        int start = numPerPage * (page - 1);
        end += start;
        for (int i = start; i < end; i++) {
            ItemStack mailboxItem = mailboxItems.get(i);
            int restI = i % numPerPage;

            int xPosition = standardX + restI % 9 * intervalX - 10 - 2;
            int yPosition = standardY + restI / 9 * intervalY;
            int scaledMouseX = (int) ((mouseX - xPosition) / scale);
            int scaledMouseY = (int) ((mouseY - yPosition) / scale);

            if (scaledMouseX >= 0 && scaledMouseX <= 16 && scaledMouseY >= 0 && scaledMouseY <= 16) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, mailboxItem, mouseX, mouseY);
            }
        }
    }
}
