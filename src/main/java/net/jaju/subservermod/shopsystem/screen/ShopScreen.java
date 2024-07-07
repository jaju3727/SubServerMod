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
    private static final ResourceLocation TEXTURE = new ResourceLocation(Subservermod.MOD_ID, "textures/gui/shop.png");
    private final Player player;
    private List<ShopItem> shopItems;
    private String entityName;
    private final int standardX = 50;
    private final int standardY = 50;
    private final int intervalX = 100;
    private final int intervalY = 40;
    private int page = 1;
    private int maxPage;
    private final int minPage = 1;

    public ShopScreen(ShopContainer screenContainer, Inventory inv, Component titleIn, Player player) {
        super(screenContainer, inv, titleIn);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.player = player;
    }

    public void setShopData(List<ShopItem> shopItems, String entityName) {
        this.shopItems = shopItems;
        this.entityName = entityName;
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
            ItemStack itemStack = new ItemStack(shopItem.getItem());
            int j = i;
            // Item name button
            this.addRenderableWidget(new CustomPlainTextButton(
                    standardX + i/4*intervalX, standardY + i%4*intervalY,
                    0, 0,
                    Component.literal(itemStack.getHoverName().getString()),
                    button -> {},
                    this.font,
                    1.0f
            ));

            if (shopItem.getIsBuyable()) {
                this.addRenderableWidget(new ImageButton(standardX + 35 + i/4*intervalX,
                        standardY+10 + i%4*intervalY,
                        30, 15, 0, 0, 0,
                        new ResourceLocation(Subservermod.MOD_ID, "textures/gui/buy.png"),
                        30, 15, button -> {
                    boolean isShiftPressed = Screen.hasShiftDown();
                    buyButtonClick((page-1)*8 + j, isShiftPressed);
                }));
            }

            if (shopItem.getIsSellable()) {
                this.addRenderableWidget(new ImageButton(standardX + 5 + i/4*intervalX,
                        standardY+10 + i%4*intervalY,
                        30, 15, 0, 0, 0,
                        new ResourceLocation(Subservermod.MOD_ID, "textures/gui/sell.png"),
                        30, 15, button -> {
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

        int boughtAmount = addItemsToInventory(player, shopItem.getItem(), buyCount);
        if (boughtAmount > 0) {
            int totalPrice = boughtAmount * shopItem.getBuyPrice();
            player.sendSystemMessage(Component.literal("Bought " + boughtAmount + " " + shopItem.getItem().getDescription().getString() + " for " + totalPrice + " coins."));
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

        // 플레이어의 인벤토리에서 아이템을 찾고 판매 로직을 수행
        int soldAmount = removeItemsFromInventory(player, shopItem.getItem(), sellCount);

        if (soldAmount > 0) {
            int totalPrice = soldAmount * shopItem.getSellPrice();
            // 플레이어에게 판매된 아이템 수와 가격을 알림
            player.sendSystemMessage(Component.literal("Sold " + soldAmount + " " + shopItem.getItem().getDescription().getString() + " for " + totalPrice + " coins."));
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                ModNetworking.INSTANCE.sendToServer(new UpdateInventoryPacket(i, stack));
            }
        } else {
            player.sendSystemMessage(Component.literal("Not enough items to sell."));
        }
    }

    // 플레이어의 인벤토리에서 특정 아이템을 제거하는 메서드
    private int removeItemsFromInventory(Player player, Item item, int count) {
        int remainingCount = count;
        int totalRemoved = 0;
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) { // 장비 칸을 제외한 인벤토리 슬롯만을 대상으로
            ItemStack stack = inventory.items.get(i);
            if (stack.getItem() == item) {
                int stackCount = stack.getCount();

                if (stackCount >= remainingCount) {
                    stack.shrink(remainingCount);
                    totalRemoved += remainingCount;
                    remainingCount = 0;
                    if (stack.getCount() == 0) {
                        inventory.items.set(i, ItemStack.EMPTY); // 아이템이 0개가 되면 빈 슬롯으로 설정
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

    private int addItemsToInventory(Player player, Item item, int count) {
        int remainingCount = count;
        int totalAdded = 0;
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) { // 장비 칸을 제외한 인벤토리 슬롯만을 대상으로
            ItemStack stack = inventory.items.get(i);
            if (stack.isEmpty()) {
                int addCount = Math.min(remainingCount, item.getMaxStackSize());
                inventory.items.set(i, new ItemStack(item, addCount));
                totalAdded += addCount;
                remainingCount -= addCount;
                if (remainingCount <= 0) {
                    break;
                }
            } else if (stack.getItem() == item && stack.getCount() < stack.getMaxStackSize()) {
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
        RenderSystem.setShaderTexture(0, TEXTURE);
        renderShopItems(guiGraphics);
    }

    private void renderShopItems(GuiGraphics guiGraphics) {
        int i = 0;
        float scale = 1.7f;

        for (ShopItem shopItem : shopItems) {
            ItemStack itemStack = new ItemStack(shopItem.getItem());
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
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
