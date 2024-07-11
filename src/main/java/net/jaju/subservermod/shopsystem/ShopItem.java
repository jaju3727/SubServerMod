package net.jaju.subservermod.shopsystem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShopItem {
    private final ItemStack itemStack;
    private final int buyPrice;
    private final int sellPrice;
    private final boolean isBuyable;
    private final boolean isSellable;

    public ShopItem(ItemStack itemStack, int buyPrice, int sellPrice, boolean isBuyable, boolean isSellable) {
        this.itemStack = itemStack;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.isBuyable = isBuyable;
        this.isSellable = isSellable;
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

    public boolean getIsBuyable() {
        return isBuyable;
    }

    public boolean getIsSellable() {
        return isSellable;
    }
}
