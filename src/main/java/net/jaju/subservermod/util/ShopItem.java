package net.jaju.subservermod.util;

import net.minecraft.world.item.ItemStack;

public class ShopItem {
    private final ItemStack itemStack;
    private final int buyPrice;
    private final int sellPrice;
    private final int dailyBuyLimitNum;
    private int dailyBuyLimitPlayerNum;
    private final int dailySellLimitNum;
    private int dailySellLimitPlayerNum;
    private final boolean isBuyable;
    private final boolean isSellable;
    private final boolean isDailyBuyLimit;
    private final boolean isDailySellLimit;
    private String coinType;

    public ShopItem(ItemStack itemStack, int buyPrice, int sellPrice, int dailyBuyLimitNum, int dailyBuyLimitPlayerNum, int dailySellLimitNum, int dailySellLimitPlayerNum, boolean isBuyable, boolean isSellable, boolean isDailyBuyLimit, boolean isDailySellLimit, String coinType) {
        this.itemStack = itemStack;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.dailyBuyLimitNum = dailyBuyLimitNum;
        this.dailyBuyLimitPlayerNum = dailyBuyLimitPlayerNum;
        this.dailySellLimitNum = dailySellLimitNum;
        this.dailySellLimitPlayerNum = dailySellLimitPlayerNum;
        this.isBuyable = isBuyable;
        this.isSellable = isSellable;
        this.isDailyBuyLimit = isDailyBuyLimit;
        this.isDailySellLimit = isDailySellLimit;
        this.coinType = coinType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public int getDailyBuyLimitNum() {
        return dailyBuyLimitNum;
    }

    public int getDailyBuyLimitPlayerNum() {
        return dailyBuyLimitPlayerNum;
    }

    public void setDailyBuyLimitPlayerNum(int dailyBuyLimitPlayerNum) {
        this.dailyBuyLimitPlayerNum = dailyBuyLimitPlayerNum;
    }

    public int getDailySellLimitNum() {
        return dailySellLimitNum;
    }

    public int getDailySellLimitPlayerNum() {
        return dailySellLimitPlayerNum;
    }

    public void setDailySellLimitPlayerNum(int dailySellLimitPlayerNum) {
        this.dailySellLimitPlayerNum = dailySellLimitPlayerNum;
    }

    public boolean getIsBuyable() {
        return isBuyable;
    }

    public boolean getIsSellable() {
        return isSellable;
    }

    public boolean getIsDailyBuyLimit() {
        return isDailyBuyLimit;
    }

    public boolean getIsDailySellLimit() {
        return isDailySellLimit;
    }

    public String getCoinType() {
        return coinType;
    }
}
