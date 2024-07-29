package net.jaju.subservermod.auction;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.auction.network.ClientAuctionHandler;
import net.jaju.subservermod.auction.network.packet.UpdateAuctionPacket;
import net.jaju.subservermod.coinsystem.CoinData;
import net.jaju.subservermod.coinsystem.network.CoinDataRequestFromAuctionPacket;
import net.jaju.subservermod.coinsystem.network.CoinDataUpdatePacket;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.mailbox.MailboxManager;
import net.jaju.subservermod.shopsystem.network.UpdateInventoryPacket;
import net.jaju.subservermod.util.ItemStackSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuctionScreen extends Screen {
    private final Player player;
    private List<AuctionItem> auctionItems;
    private AuctionItem buyAuctionItem = null;
    private final int standardX = 117;
    private final int standardY = 68;
    private final int intervalX = 31;
    private final int intervalY = 30;
    private int page = 1;
    private int maxPage;
    private final int minPage = 1;
    private static final int numPerPage = 54;
    private CoinData coinData;

    public AuctionScreen(Player player) {
        super(Component.literal("Auction"));
        this.player = player;
        auctionItems = new ArrayList<>(ClientAuctionHandler.getAuctionItems());
        this.maxPage = (auctionItems.size() - 1) / numPerPage + 1;
        requestCoinDataFromServer();
    }

    private void requestCoinDataFromServer() {
        ModNetworking.INSTANCE.sendToServer(new CoinDataRequestFromAuctionPacket());
    }

    public void updateCoinData(CoinData coinData) {
        this.coinData = coinData;
    }

    @Override
    protected void init() {
        super.init();
    }

    private void ItemClick(AuctionItem buyAuctionItem) {
        this.buyAuctionItem = buyAuctionItem;
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

    private void buyClick() {
        ItemStack itemStack = ItemStackSerializer.deserialize(buyAuctionItem.item()).copy();
        int price = buyAuctionItem.coinNum();
        String coinType = buyAuctionItem.coinType();
        System.out.println(buyAuctionItem+"    "+itemStack);

        if (!hasEnoughCoins(coinType, price)) {
            player.sendSystemMessage(Component.literal("코인이 충분하지 않습니다."));
            return;
        }

        int boughtAmount = addItemsToInventory(player, itemStack);

        if (boughtAmount > 0) {
            player.sendSystemMessage(Component.literal("Bought " + boughtAmount + " " + itemStack.getHoverName().getString() + " for " + price + " coins."));

            removeCoins(coinType, price);
            ModNetworking.INSTANCE.sendToServer(new CoinDataUpdatePacket(coinData, player.getUUID()));

            ItemStack coinItem = switch (coinType) {
                case "sub_coin" -> new ItemStack(ModItem.SUB_COIN.get());
                case "chef_coin" -> new ItemStack(ModItem.CHEF_COIN.get());
                case "farmer_coin" -> new ItemStack(ModItem.FARMER_COIN.get());
                case "fisherman_coin" -> new ItemStack(ModItem.FISHERMAN_COIN.get());
                case "alchemist_coin" -> new ItemStack(ModItem.ALCHEMIST_COIN.get());
                case "miner_coin" -> new ItemStack(ModItem.MINER_COIN.get());
                case "woodcutter_coin" -> new ItemStack(ModItem.WOODCUTTER_COIN.get());
                default -> throw new IllegalStateException("Unexpected value: " + coinType);
            };
            MutableComponent lore = Component.literal(price + "원");
            ListTag loreList = new ListTag();
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(lore)));

            coinItem.getOrCreateTagElement("display").put("Lore", loreList);

            MailboxManager.getInstance().addItemToMailbox(buyAuctionItem.playerUUID(), coinItem);
            auctionItems.remove(buyAuctionItem);
            List<AuctionItem> updatedAuctionItems = new ArrayList<>(auctionItems);
            buyAuctionItem = null;
            ModNetworking.INSTANCE.sendToServer(new UpdateAuctionPacket(updatedAuctionItems));

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                ModNetworking.INSTANCE.sendToServer(new UpdateInventoryPacket(i, stack));
            }
        } else {
            player.sendSystemMessage(Component.literal("Not enough space to buy."));
        }
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
        }
    }

    private void leftPage() {
        if (page > minPage) {
            page--;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (buyAuctionItem == null) {
            if (button == 0) {
                float scale = 1.4f;
                int start = (page - 1) * numPerPage;
                int end = Math.min(start + numPerPage, auctionItems.size());
                for (int i = start; i < end; i++) {
                    int restI = i % numPerPage;
                    int xPosition = standardX + restI % 9 * intervalX - 10 - 2;
                    int yPosition = standardY + restI / 9 * intervalY;
                    int scaledMouseX = (int) ((mouseX - xPosition) / scale);
                    int scaledMouseY = (int) ((mouseY - yPosition) / scale);

                    if (scaledMouseX >= 0 && scaledMouseX <= 16 && scaledMouseY >= 0 && scaledMouseY <= 16) {
                        ItemClick(auctionItems.get(i));
                        return true;
                    }
                }

                int x = 175;
                int rightArrowX = this.width / 2 + x - 30;
                int leftArrowX = this.width / 2 - x;
                int arrowY = this.height / 2;

                if (mouseX >= rightArrowX && mouseX <= rightArrowX + 30 && mouseY >= arrowY && mouseY <= arrowY + 15) {
                    rightPage();
                    return true;
                } else if (mouseX >= leftArrowX && mouseX <= leftArrowX + 30 && mouseY >= arrowY && mouseY <= arrowY + 15) {
                    leftPage();
                    return true;
                }
            }
        } else {
            if (button == 0) {
                int x = 100;
                int buyButtonX = this.width / 2 - x;
                int cancelButtonX = this.width / 2 + x - 60;
                int arrowY = this.height / 2;

                if (mouseX >= buyButtonX && mouseX <= buyButtonX + 60 && mouseY >= arrowY && mouseY <= arrowY + 30) {
                    buyClick();
                    return true;
                } else if (mouseX >= cancelButtonX && mouseX <= cancelButtonX + 60 && mouseY >= arrowY && mouseY <= arrowY + 30) {
                    buyAuctionItem = null;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        auctionItems = new ArrayList<>(ClientAuctionHandler.getAuctionItems());
        this.maxPage = (auctionItems.size() - 1) / numPerPage + 1;
        if (buyAuctionItem == null) {
            int textureWidth = 300;
            int textureHeight = (int) (textureWidth * 0.8333);
            int posX = (this.width - textureWidth) / 2;
            int posY = (this.height - textureHeight) / 2;
            int x = 175;
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/auction/auction_background.png"),
                    posX, posY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
            if (page != minPage) {
                guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/leftarrow.png"),
                        this.width / 2 - x, this.height / 2, 0, 0, 30, 15, 30, 15);
            }
            if (page != maxPage) {
                guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/rightarrow.png"),
                        this.width / 2 + x - 30, this.height / 2, 0, 0, 30, 15, 30, 15);
            }
            renderAuctionItems(guiGraphics);
            renderTooltipsForItems(guiGraphics, mouseX, mouseY);
        } else {
            int x = 100;
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/buy.png"),
                    this.width / 2 - x, this.height / 2, 0, 0, 60, 30, 60, 30);
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/cancel.png"),
                    this.width / 2 + x - 60, this.height / 2, 0, 0, 60, 30, 60, 30);
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void renderAuctionItems(GuiGraphics guiGraphics) {
        float scale = 1.4f;
        int start = (page - 1) * numPerPage;
        int end = Math.min(start + numPerPage, auctionItems.size());
        for (int i = start; i < end; i++) {
            AuctionItem auctionItem = auctionItems.get(i);
            int restI = i % numPerPage;
            int xPosition = standardX + restI % 9 * intervalX - 10 - 2;
            int yPosition = standardY + restI / 9 * intervalY;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(scale, scale, scale);

            ItemStack itemStack = ItemStackSerializer.deserialize(auctionItem.item());
            guiGraphics.renderItem(itemStack, 0, 0);

            poseStack.translate(10, 10, 200);
            String countLabel = String.valueOf(itemStack.getCount());
            guiGraphics.drawString(Minecraft.getInstance().font, countLabel, 0, 0, 0xFFFFFF);

            poseStack.popPose();
        }
    }

    private void renderTooltipsForItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        float scale = 1.4f;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초"); // 한국어 날짜 형식 지정

        int start = (page - 1) * numPerPage;
        int end = Math.min(start + numPerPage, auctionItems.size());
        for (int i = start; i < end; i++) {
            AuctionItem auctionItem = auctionItems.get(i);
            int restI = i % numPerPage;

            int xPosition = standardX + restI % 9 * intervalX - 10 - 2;
            int yPosition = standardY + restI / 9 * intervalY;
            int scaledMouseX = (int) ((mouseX - xPosition) / scale);
            int scaledMouseY = (int) ((mouseY - yPosition) / scale);

            if (scaledMouseX >= 0 && scaledMouseX <= 16 && scaledMouseY >= 0 && scaledMouseY <= 16) {
                ItemStack itemStack = ItemStackSerializer.deserialize(auctionItem.item());

                CompoundTag displayTag = itemStack.getOrCreateTagElement("display");
                ListTag loreTag = new ListTag();

                loreTag.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));
                loreTag.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("§9| ").withStyle(Style.EMPTY.withColor(0x0000FF))
                        .append(Component.literal("출매자: " + auctionItem.playerName()).withStyle(Style.EMPTY.withColor(0xFFFFFF))))));
                loreTag.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("§9| ").withStyle(Style.EMPTY.withColor(0x0000FF))
                        .append(Component.literal("출매일: "+ dateFormat.format(new Date(auctionItem.timestamp()))).withStyle(Style.EMPTY.withColor(0xFFFFFF))))));

                String coinType_KO_KR = switch (auctionItem.coinType()) {
                    case "sub_coin" -> "섭코인";
                    case "alchemist_coin" -> "연금술사 코인";
                    case "chef_coin" -> "요리사 코인";
                    case "farmer_coin" -> "농부 코인";
                    case "fisherman_coin" -> "어부 코인";
                    case "miner_coin" -> "광부 코인";
                    case "woodcutter_coin" -> "나무꾼 코인";
                    default -> "null";
                };

                loreTag.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("§9| ").withStyle(Style.EMPTY.withColor(0x0000FF))
                        .append(Component.literal(coinType_KO_KR + ": " + auctionItem.coinNum() + "개").withStyle(Style.EMPTY.withColor(0xFFFFFF))))));

                displayTag.put("Lore", loreTag);
                itemStack.getTag().put("display", displayTag);

                guiGraphics.renderTooltip(Minecraft.getInstance().font, itemStack, mouseX, mouseY);
            }
        }
    }
}