package net.jaju.subservermod.shopsystem;

import net.minecraft.world.item.Item;

public class ShopItem {
    private final Item item;
    private final int buyPrice;
    private final int sellPrice;
    private final boolean isBuyable;
    private final boolean isSellable;

    public ShopItem(Item item, int buyPrice, int sellPrice, boolean isBuyable, boolean isSellable) {
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.isBuyable = isBuyable;
        this.isSellable = isSellable;
    }

    public Item getItem() {
        return item;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public boolean getIsBuyable() {
        return isBuyable;
    }

    public boolean getIsSellable() {
        return isSellable;
    }
}
