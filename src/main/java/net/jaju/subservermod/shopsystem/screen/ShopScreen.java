package net.jaju.subservermod.shopsystem.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.shopsystem.ShopItem;
import net.jaju.subservermod.shopsystem.network.UpdateInventoryPacket;
import net.jaju.subservermod.util.CustomPlainTextButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ShopScreen extends AbstractContainerScreen<ShopContainer> {
    private final Player player;
    private List<ShopItem> shopItems;
    private final int standardX = 79;
    private final int standardY = 70;
    private final int intervalX = 108;
    private final int intervalY = 40;
    private int page = 1;
    private int maxPage;
    private final int minPage = 1;

    public ShopScreen(ShopContainer screenContainer, Inventory inv, Component titleIn, Player player) {
        super(screenContainer, inv, titleIn);
        this.player = player;
    }

    public void setShopData(List<ShopItem> shopItems, String entityName) {
        this.shopItems = shopItems;
        maxPage = (shopItems.size() - 1) / 8 + 1;
        initialize();
    }

    @Override
    protected void init() {
        super.init();
    }

    private void initialize() {



        int i = 0;
        for (ShopItem shopItem : shopItems) {
            int j = i;

            if (shopItem.getIsBuyable()) {
                this.addRenderableWidget(new ImageButton(standardX + 7 + i/4*intervalX,
                        standardY - 12 + i%4*intervalY,
                        23, 12, 0, 0, 0,
                        new ResourceLocation(Subservermod.MOD_ID, "textures/gui/buy.png"),
                        23, 12, button -> {
                    boolean isShiftPressed = Screen.hasShiftDown();
                    buyButtonClick((page-1)*8 + j, isShiftPressed);
                }));
            }

            if (shopItem.getIsSellable()) {
                this.addRenderableWidget(new ImageButton(standardX + 7 + i/4*intervalX,
                        standardY + 2 + i%4*intervalY,
                        23, 12, 0, 0, 0,
                        new ResourceLocation(Subservermod.MOD_ID, "textures/gui/sell.png"),
                        23, 12, button -> {
                    boolean isShiftPressed = Screen.hasShiftDown();
                    boolean isAltPressed = Screen.hasAltDown();
                    sellButtonClick((page-1)*8 + j, isShiftPressed, isAltPressed);
                }));
            }

            i++;
        }

        if (page != minPage) {
            this.addRenderableWidget(new ImageButton(40, 190,
                    10, 10, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/leftarrow.png"),
                    10, 10, button -> leftPage()));
        }
        if (page != maxPage) {
            this.addRenderableWidget(new ImageButton(150, 190,
                    10, 10, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/rightarrow.png"),
                    10, 10, button -> rightPage()));
        }
    }

    private void buyButtonClick(int itemIndex, boolean isShiftPressed) {
        int buyCount = isShiftPressed ? 64 : 1;
        ShopItem shopItem = shopItems.get(itemIndex);
        ItemStack itemStack = shopItem.getItemStack().copy();
        itemStack.setCount(buyCount);

        int boughtAmount = addItemsToInventory(player, itemStack);
        if (boughtAmount > 0) {
            int totalPrice = boughtAmount * shopItem.getBuyPrice();
            player.sendSystemMessage(Component.literal("Bought " + boughtAmount + " " + shopItem.getItemStack().getHoverName().getString() + " for " + totalPrice + " coins."));
            // 서버로 인벤토리 업데이트 패킷 전송
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                ModNetworking.INSTANCE.sendToServer(new UpdateInventoryPacket(i, stack));
            }
        } else {
            player.sendSystemMessage(Component.literal("Not enough space to buy."));
        }
    }

    private void sellButtonClick(int itemIndex, boolean isShiftPressed, boolean isAltPressed) {
        ShopItem shopItem = shopItems.get(itemIndex);
        int sellCount = isShiftPressed ? 64 : isAltPressed ? Integer.MAX_VALUE : 1;
        ItemStack itemStack = shopItem.getItemStack().copy();
        itemStack.setCount(sellCount);

        // 플레이어의 인벤토리에서 아이템을 찾고 판매 로직을 수행
        int soldAmount = removeItemsFromInventory(player, itemStack);

        if (soldAmount > 0) {
            int totalPrice = soldAmount * shopItem.getSellPrice();
            // 플레이어에게 판매된 아이템 수와 가격을 알림
            player.sendSystemMessage(Component.literal("Sold " + soldAmount + " " + shopItem.getItemStack().getHoverName().getString() + " for " + totalPrice + " coins."));
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                ModNetworking.INSTANCE.sendToServer(new UpdateInventoryPacket(i, stack));
            }
        } else {
            player.sendSystemMessage(Component.literal("Not enough items to sell."));
        }
    }

    // 플레이어의 인벤토리에서 특정 아이템을 제거하는 메서드
    private int removeItemsFromInventory(Player player, ItemStack itemStack) {
        int remainingCount = itemStack.getCount();
        int totalRemoved = 0;
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack stack = inventory.items.get(i);
            if (ItemStack.isSameItemSameTags(stack, itemStack)) {
                int stackCount = stack.getCount();

                if (stackCount >= remainingCount) {
                    stack.shrink(remainingCount);
                    totalRemoved += remainingCount;
                    remainingCount = 0;
                    if (stack.getCount() == 0) {
                        inventory.items.set(i, ItemStack.EMPTY);
                    }
                    break;
                } else {
                    stack.shrink(stackCount);
                    totalRemoved += stackCount;
                    remainingCount -= stackCount;
                    inventory.items.set(i, ItemStack.EMPTY); // 아이템이 0개가 되면 빈 슬롯으로 설정
                }
            }
        }

        return totalRemoved;
    }

    private int addItemsToInventory(Player player, ItemStack itemStack) {
        int remainingCount = itemStack.getCount();
        int totalAdded = 0;
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) { // 장비 칸을 제외한 인벤토리 슬롯만을 대상으로
            ItemStack stack = inventory.items.get(i);
            if (stack.isEmpty()) {
                int addCount = Math.min(remainingCount, itemStack.getMaxStackSize());
                ItemStack newStack = itemStack.copy();
                newStack.setCount(addCount);
                inventory.items.set(i, newStack);
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
            initialize();
        }
    }

    private void leftPage() {
        if (page > minPage) {
            page--;
            initialize();
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {

        renderShopItems(guiGraphics);
    }

    private void renderShopItems(GuiGraphics guiGraphics) {
        int i = 0;
        float scale = 1.7f;

        for (ShopItem shopItem : shopItems) {
            ItemStack itemStack = shopItem.getItemStack();
            int xPosition = standardX + i / 4 * intervalX - 10;
            int yPosition = standardY + i % 4 * intervalY;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-8, -8, 0);

            guiGraphics.renderItem(itemStack, 0, 0);

            poseStack.popPose();
            i++;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        int textureWidth = 400;
        int textureHeight = 190;
        int posX = (this.width - textureWidth) / 2;
        int posY = (this.height - textureHeight) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/shopsystem/shopbackground.png"),
                posX, posY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderTooltipsForItems(guiGraphics, mouseX, mouseY);
    }

    private void renderTooltipsForItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int i = 0;
        float scale = 1.7f;

        for (ShopItem shopItem : shopItems) {
            int xPosition = standardX + i / 4 * intervalX - 10;
            int yPosition = standardY + i % 4 * intervalY;
            int scaledMouseX = (int) ((mouseX - xPosition) / scale);
            int scaledMouseY = (int) ((mouseY - yPosition) / scale);

            if (scaledMouseX >= -8 && scaledMouseX <= 8 && scaledMouseY >= -8 && scaledMouseY <= 8) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, shopItem.getItemStack(), mouseX, mouseY);
            }
            i++;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Do nothing to prevent rendering the default text
    }
}
