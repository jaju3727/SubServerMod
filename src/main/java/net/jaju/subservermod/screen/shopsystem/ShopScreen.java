package net.jaju.subservermod.screen.shopsystem;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.network.coinsystem.CoinDataRequestFromShopPacket;
import net.jaju.subservermod.network.coinsystem.CoinDataUpdatePacket;
import net.jaju.subservermod.entity.ShopEntity;
import net.jaju.subservermod.util.ShopItem;
import net.jaju.subservermod.events.shopsystem.SalesDataHandler;
import net.jaju.subservermod.network.shopsystem.UpdateInventoryPacket;
import net.jaju.subservermod.network.shopsystem.UpdateShopEntityPacket;
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
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShopScreen extends AbstractContainerScreen<ShopContainer> {
    private final Player player;
    private final ShopEntity shopEntity;
    private List<ShopItem> shopItems;
    private CoinData coinData = null;
    private final int standardX = 79;
    private final int standardY = 70;
    private final int intervalX = 108;
    private final int intervalY = 40;
    private int page = 1;
    private int maxPage;
    private final int minPage = 1;
    private static final int numPerPage = 8;

    public ShopScreen(ShopContainer screenContainer, Inventory inv, Component titleIn, Player player) {
        super(screenContainer, inv, titleIn);
        this.player = player;
        shopEntity = screenContainer.getShopEntity();
        requestCoinDataFromServer();
    }

    private void requestCoinDataFromServer() {
        ModNetworking.INSTANCE.sendToServer(new CoinDataRequestFromShopPacket());
    }

    public void updateCoinData(CoinData coinData) {
        this.coinData = coinData;
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
        this.clearWidgets();

        int end = page == maxPage ? (shopItems.size() - 1) % numPerPage + 1 : numPerPage;
        int start = numPerPage * (page - 1);
        end += start;
        int i = 0;
        for (ShopItem shopItem : shopItems) {
            if (i < start) {
                i++;
                continue;
            }
            if (i >= end) break;
            int j = i;
            int restI = i % numPerPage;

            if (shopItem.getIsBuyable()) {
                this.addRenderableWidget(new ImageButton(standardX + 7 + restI/4*intervalX,
                        standardY - 12 + restI%4*intervalY,
                        23, 12, 0, 0, 0,
                        new ResourceLocation(Subservermod.MOD_ID, "textures/gui/buy.png"),
                        23, 12, button -> {
                    boolean isShiftPressed = Screen.hasShiftDown();
                    buyButtonClick(j, isShiftPressed);
                }));
                this.addRenderableWidget(new CustomPlainTextButton(standardX + 32 + restI/4*intervalX,
                          standardY - 12 + restI%4*intervalY + 2,
                        0, 0, Component.literal(String.valueOf(shopItem.getBuyPrice())), button -> {}, this.font,  1.0f, 0xA4A4A4));
            }

            if (shopItem.getIsSellable()) {
                this.addRenderableWidget(new ImageButton(standardX + 7 + restI/4*intervalX,
                        standardY + 2 + restI%4*intervalY,
                        23, 12, 0, 0, 0,
                        new ResourceLocation(Subservermod.MOD_ID, "textures/gui/sell.png"),
                        23, 12, button -> {
                    boolean isShiftPressed = Screen.hasShiftDown();
                    sellButtonClick(j, isShiftPressed);
                }));
                this.addRenderableWidget(new CustomPlainTextButton(standardX + 32 + restI/4*intervalX,
                        standardY + 2 + restI%4*intervalY + 2,
                        0, 0, Component.literal(String.valueOf(shopItem.getSellPrice())), button -> {}, this.font,  1.0f, 0xA4A4A4));
            }

            this.addRenderableWidget(new ImageButton(standardX - 2 + restI/4*intervalX,
                    standardY - 14 + restI%4*intervalY,
                    8, 8, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/item/"+shopItem.getCoinType()+".png"),
                    8, 8, button -> {
            }));

            i++;
        }

        if (page != minPage) {
            this.addRenderableWidget(new ImageButton(137, 210,
                    10, 10, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/leftarrow.png"),
                    10, 10, button -> leftPage()));
        }
        if (page != maxPage) {
            this.addRenderableWidget(new ImageButton(158, 210,
                    10, 10, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/rightarrow.png"),
                    10, 10, button -> rightPage()));
        }
    }

    public boolean hasEnoughCoins(String type, int amount) {
        return switch (type) {
            case "sub_coin" -> coinData.getSubcoin() >= amount;
            case "chef_coin" -> coinData.getChefcoin() >= amount;
            case "farmer_coin" -> coinData.getFarmercoin() >= amount;
            case "fisherman_coin" -> coinData.getFishermancoin() >= amount;
            case "alchemist_coin" -> coinData.getAlchemistcoin() >= amount;
            case "miner_coin" -> coinData.getMinercoin() >= amount;
            case "woodcutter_coin" -> coinData.getWoodcuttercoin() >= amount;
            default -> false;
        };
    }

    public void removeCoins(String type, int amount) {
        switch (type) {
            case "sub_coin" -> coinData.setSubcoin(Math.max(coinData.getSubcoin() - amount, 0));
            case "chef_coin" -> coinData.setChefcoin(Math.max(coinData.getChefcoin() - amount, 0));
            case "farmer_coin" -> coinData.setFarmercoin(Math.max(coinData.getFarmercoin() - amount, 0));
            case "fisherman_coin" -> coinData.setFishermancoin(Math.max(coinData.getFishermancoin() - amount, 0));
            case "alchemist_coin" -> coinData.setAlchemistcoin(Math.max(coinData.getAlchemistcoin() - amount, 0));
            case "miner_coin" -> coinData.setMinercoin(Math.max(coinData.getMinercoin() - amount, 0));
            case "woodcutter_coin" -> coinData.setWoodcuttercoin(Math.max(coinData.getWoodcuttercoin() - amount, 0));
        }
    }

    public void addCoins(String type, int amount) {
        switch (type) {
            case "sub_coin" -> coinData.setSubcoin(coinData.getSubcoin() + amount);
            case "chef_coin" -> coinData.setChefcoin(coinData.getChefcoin() + amount);
            case "farmer_coin" -> coinData.setFarmercoin(coinData.getFarmercoin() + amount);
            case "fisherman_coin" -> coinData.setFishermancoin(coinData.getFishermancoin() + amount);
            case "alchemist_coin" -> coinData.setAlchemistcoin(coinData.getAlchemistcoin() + amount);
            case "miner_coin" -> coinData.setMinercoin(coinData.getMinercoin() + amount);
            case "woodcutter_coin" -> coinData.setWoodcuttercoin(coinData.getWoodcuttercoin() + amount);
        }
    }

    public int getCoins(String type) {
        return switch (type) {
            case "sub_coin" -> coinData.getSubcoin();
            case "chef_coin" -> coinData.getChefcoin();
            case "farmer_coin" -> coinData.getFarmercoin();
            case "fisherman_coin" -> coinData.getFishermancoin();
            case "alchemist_coin" -> coinData.getAlchemistcoin();
            case "miner_coin" -> coinData.getMinercoin();
            case "woodcutter_coin" -> coinData.getWoodcuttercoin();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    private int getMaxBuyableCount(Player player, ItemStack itemStack) {
        int maxCount = 0;
        Inventory inventory = player.getInventory();
        int maxStackSize = itemStack.getMaxStackSize(); // 아이템 스택의 최대 크기

        for (ItemStack stack : inventory.items) {
            if (stack.isEmpty()) {
                // 빈 슬롯일 경우, 전체 슬롯 크기만큼 추가 가능
                maxCount += maxStackSize;
            } else if (ItemStack.isSameItemSameTags(stack, itemStack)) {
                // 동일한 아이템 스택일 경우, 남은 공간만큼 추가 가능
                maxCount += maxStackSize - stack.getCount();
            }
        }

        return maxCount;
    }


    private void buyButtonClick(int itemIndex, boolean isShiftPressed) {
        ShopItem shopItem = shopItems.get(itemIndex);
        int buyCount = isShiftPressed ? 64 : 1;
        int dailyBuyLimitPlayerNum = shopItem.getDailyBuyLimitPlayerNum();
        boolean isDailyBuyLimit = shopItem.getIsDailyBuyLimit();

        if (isDailyBuyLimit) {
            if (0 < dailyBuyLimitPlayerNum) buyCount = (buyCount - dailyBuyLimitPlayerNum >= 0) ? dailyBuyLimitPlayerNum : buyCount;
            else {
                player.sendSystemMessage(Component.literal("오늘은 더 이상 이 아이템을 구매하실 수 없습니다."));
                return;
            }
        }

        ItemStack itemStack = shopItem.getItemStack().copy();
        int maxBuyableCount = getMaxBuyableCount(player, itemStack);

        if (maxBuyableCount < buyCount) {
            player.sendSystemMessage(Component.literal("공간이 부족합니다."));
            return;
        }

        itemStack.setCount(buyCount);

        int totalPrice = buyCount * shopItem.getBuyPrice();
        String coinType = shopItem.getCoinType();

        if (!hasEnoughCoins(coinType, totalPrice)) {
            player.sendSystemMessage(Component.literal("코인이 충분하지 않습니다."));
            return;
        }

        int boughtAmount = addItemsToInventory(player, itemStack);
        if (boughtAmount > buyCount - 1) {
            if (isDailyBuyLimit) {
                shopItem.setDailyBuyLimitPlayerNum(dailyBuyLimitPlayerNum - buyCount);
                shopItems.set(itemIndex, shopItem);
                ModNetworking.INSTANCE.sendToServer(new UpdateShopEntityPacket(shopEntity.getId(), itemIndex, shopItem.getDailyBuyLimitPlayerNum(), shopItem.getDailySellLimitPlayerNum()));
            }
            player.sendSystemMessage(Component.literal( shopItem.getItemStack().getHoverName().getString() + " " + boughtAmount + "개를 " + coinType + " " + totalPrice + "원으로 구매하셨습니다."));

            removeCoins(coinType, totalPrice);
            ModNetworking.INSTANCE.sendToServer(new CoinDataUpdatePacket(coinData, player.getUUID()));

//            SoundPlayer.playCustomSound(player, new ResourceLocation(Subservermod.MOD_ID, "buying_sound"), 2.0f, 2.0f);

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                ModNetworking.INSTANCE.sendToServer(new UpdateInventoryPacket(i, stack));
            }
        } else {
            player.sendSystemMessage(Component.literal("공간이 부족합니다."));
        }
    }

    private void sellButtonClick(int itemIndex, boolean isShiftPressed) {
        ShopItem shopItem = shopItems.get(itemIndex);
        ItemStack itemStack = shopItem.getItemStack().copy();
        int unitsPerCoin = itemStack.getCount();
        int sellPricePerUnit = shopItem.getSellPrice();
        int dailySellLimitPlayerNum = shopItem.getDailySellLimitPlayerNum();
        boolean isDailySellLimit = shopItem.getIsDailySellLimit();

        int maxSellableCount = getMaxSellableCount(player, itemStack);

        if (maxSellableCount < unitsPerCoin) {
            player.sendSystemMessage(Component.literal("아이템 개수가 부족합니다."));
            return;
        }

        int maxSellableUnits = maxSellableCount / unitsPerCoin;
        int sellCount = isShiftPressed ? maxSellableUnits * unitsPerCoin : unitsPerCoin;

        if (isDailySellLimit) {
            if (dailySellLimitPlayerNum > 0) {
                sellCount = Math.min(sellCount, dailySellLimitPlayerNum);
            } else {
                player.sendSystemMessage(Component.literal("오늘은 더 이상 이 아이템을 판매하실 수 없습니다."));
                return;
            }
        }

        itemStack.setCount(sellCount);

        String coinType = shopItem.getCoinType();

        int soldAmount = removeItemsFromInventory(player, itemStack);

        if (soldAmount > 0) {
            if (isDailySellLimit) {
                shopItem.setDailySellLimitPlayerNum(dailySellLimitPlayerNum - sellCount);
                shopItems.set(itemIndex, shopItem);
                ModNetworking.INSTANCE.sendToServer(new UpdateShopEntityPacket(shopEntity.getId(), itemIndex, shopItem.getDailyBuyLimitPlayerNum(), shopItem.getDailySellLimitPlayerNum()));
            }
            int totalPrice = (soldAmount / unitsPerCoin) * sellPricePerUnit;
            player.sendSystemMessage(Component.literal( shopItem.getItemStack().getHoverName().getString() + " " + soldAmount + "개를 " + coinType + " " + totalPrice + "원으로 판매하셨습니다."));

//            SoundPlayer.playCustomSound(player, new ResourceLocation(Subservermod.MOD_ID, "buying_sound"), 2.0f, 2.0f);

            addCoins(coinType, totalPrice);
            ModNetworking.INSTANCE.sendToServer(new CoinDataUpdatePacket(coinData, player.getUUID()));

            SalesDataHandler.recordSale(player.getUUID().toString(), coinType, totalPrice);

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                ModNetworking.INSTANCE.sendToServer(new UpdateInventoryPacket(i, stack));
            }
        } else {
            player.sendSystemMessage(Component.literal("아이템 개수가 부족합니다."));
        }
    }

    private int getMaxSellableCount(Player player, ItemStack itemStack) {
        int totalCount = 0;
        Inventory inventory = player.getInventory();

        for (ItemStack stack : inventory.items) {
            if (ItemStack.isSameItemSameTags(stack, itemStack)) {
                totalCount += stack.getCount();
            }
        }

        return totalCount;
    }

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
                    if (stack.getCount() == 0) {
                        inventory.items.set(i, ItemStack.EMPTY);
                    }
                    break;
                } else {
                    stack.shrink(stackCount);
                    totalRemoved += stackCount;
                    remainingCount -= stackCount;
                    inventory.items.set(i, ItemStack.EMPTY);
                }
            }
        }

        return totalRemoved;
    }

    private int addItemsToInventory(Player player, ItemStack itemStack) {
        int remainingCount = itemStack.getCount();
        int totalAdded = 0;
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) {
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

    }

    private void renderShopItems(GuiGraphics guiGraphics) {
        float scale = 1.4f;
        int end = page == maxPage ? (shopItems.size() - 1) % numPerPage + 1 : numPerPage;
        int start = numPerPage * (page - 1);
        end += start;
        int i = 0;
        for (ShopItem shopItem : shopItems) {
            if (i < start) {
                i++;
                continue;
            }
            if (i >= end) break;
            int restI = i % numPerPage;
            ItemStack itemStack = shopItem.getItemStack();
            int xPosition = standardX + restI / 4 * intervalX - 10 - 2;
            int yPosition = standardY + restI % 4 * intervalY;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-8, -8, 0);

            guiGraphics.renderItem(itemStack, 0, 0);

            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(0.8f, 0.8f, 0.8f);

            if (shopItem.getIsDailyBuyLimit()) {
                guiGraphics.drawString(this.font, shopItem.getDailyBuyLimitPlayerNum() + "/" + shopItem.getDailyBuyLimitNum(),
                        55, -10, 0xFFFFFF);

            }
            if (shopItem.getIsDailySellLimit()) {
                guiGraphics.drawString(this.font, shopItem.getDailySellLimitPlayerNum() + "/" + shopItem.getDailySellLimitNum(),
                        55,6, 0xFFFFFF);
            }
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(0.8f, 0.8f, 0.8f);
            guiGraphics.drawCenteredString(this.font, String.valueOf(shopItem.getItemStack().getCount()),
                    16, 10, 0xA4A4A4);
            poseStack.popPose();
            i++;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (coinData == null) {
            return;
        }
        this.renderBackground(guiGraphics);
        int textureWidth = 400;
        int textureHeight = (int) (textureWidth * 0.487);
        int posX = (this.width - textureWidth) / 2;
        int posY = (this.height - textureHeight) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/shopsystem/shopbackground.png"),
                posX, posY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        int i = 0;

        posX += 235;
        posY += 100;

        for (String type: List.of("sub_coin", "farmer_coin", "chef_coin", "fisherman_coin", "alchemist_coin", "miner_coin", "woodcutter_coin")) {
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/item/" + type + ".png"),
                    posX, posY + i * 12, 0, 0, 10, 10, 10, 10);
            guiGraphics.drawString(this.font, String.valueOf(getCoins(type)), posX + 13, posY + i * 12, 0xA4A4A4);
            i++;
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderShopItems(guiGraphics);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderTooltipsForItems(guiGraphics, mouseX, mouseY);
    }

    private void renderTooltipsForItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int i = 0;
        float scale = 1.7f;

        int end = page == maxPage ? (shopItems.size() - 1) % numPerPage + 1 : numPerPage;
        int start = numPerPage * (page - 1);
        end += start;
        for (ShopItem shopItem : shopItems) {
            if (i < start) {
                i++;
                continue;
            }
            if (i >= end) break;
            int restI = i % numPerPage;

            int xPosition = standardX + restI / 4 * intervalX - 10;
            int yPosition = standardY + restI % 4 * intervalY;
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

    }
}
