package net.jaju.subservermod.shopsystem.network;

import net.jaju.subservermod.shopsystem.ShopItem;
import net.jaju.subservermod.shopsystem.entity.ShopEntity;
import net.jaju.subservermod.shopsystem.screen.ShopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ShopEntityDataPacket {
    private final int entityId;
    private final List<ShopItem> shopItems;
    private final String entityName;

    public ShopEntityDataPacket(ShopEntity entity) {
        this.entityId = entity.getId();
        this.shopItems = entity.getShopItems();
        this.entityName = entity.getName().getString();
    }

    public ShopEntityDataPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        int itemCount = buf.readInt();
        this.shopItems = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            ItemStack itemStack = buf.readItem();
            int buyPrice = buf.readInt();
            int sellPrice = buf.readInt();
            boolean isBuyable = buf.readBoolean();
            boolean isSellable = buf.readBoolean();
            this.shopItems.add(new ShopItem(itemStack, buyPrice, sellPrice, isBuyable, isSellable));
        }
        this.entityName = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(shopItems.size());
        for (ShopItem shopItem : shopItems) {
            buf.writeItem(shopItem.getItemStack());
            buf.writeInt(shopItem.getBuyPrice());
            buf.writeInt(shopItem.getSellPrice());
            buf.writeBoolean(shopItem.getIsBuyable());
            buf.writeBoolean(shopItem.getIsSellable());
        }
        buf.writeUtf(entityName);
    }

    public static void handle(ShopEntityDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            Screen currentScreen = minecraft.screen;

            if (currentScreen instanceof ShopScreen) {
                ShopScreen shopScreen = (ShopScreen) currentScreen;
                shopScreen.setShopData(packet.shopItems, packet.entityName);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
