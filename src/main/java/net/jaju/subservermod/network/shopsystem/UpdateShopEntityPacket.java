package net.jaju.subservermod.network.shopsystem;

import net.jaju.subservermod.entity.ShopEntity;
import net.jaju.subservermod.util.ShopItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateShopEntityPacket {
    private final int entityId;
    private final int itemIndex;
    private final int buyLimit;
    private final int sellLimit;

    public UpdateShopEntityPacket(int entityId, int itemIndex, int buyLimit, int sellLimit) {
        this.entityId = entityId;
        this.itemIndex = itemIndex;
        this.buyLimit = buyLimit;
        this.sellLimit = sellLimit;
    }

    public UpdateShopEntityPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.itemIndex = buf.readInt();
        this.buyLimit = buf.readInt();
        this.sellLimit = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(itemIndex);
        buf.writeInt(buyLimit);
        buf.writeInt(sellLimit);
    }

    public static void handle(UpdateShopEntityPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player != null) {
                Level world = player.level();
                Entity entity = world.getEntity(packet.entityId);
                if (entity instanceof ShopEntity) {
                    ShopEntity shopEntity = (ShopEntity) entity;
                    ShopItem shopItem = shopEntity.getShopItems().get(packet.itemIndex);
                    shopItem.setDailyBuyLimitPlayerNum(packet.buyLimit);
                    shopItem.setDailySellLimitPlayerNum(packet.sellLimit);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}