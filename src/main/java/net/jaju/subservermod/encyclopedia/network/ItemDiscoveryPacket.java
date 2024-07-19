package net.jaju.subservermod.encyclopedia.network;

import net.jaju.subservermod.encyclopedia.EncyclopediaManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

public class ItemDiscoveryPacket {
    private final String itemName;
    private final int itemCount;

    public ItemDiscoveryPacket(String itemName, int itemCount) {
        this.itemName = itemName;
        this.itemCount = itemCount;
    }

    public static ItemDiscoveryPacket decode(FriendlyByteBuf buf) {
        String itemName = buf.readUtf();
        int itemCount = buf.readInt();
        return new ItemDiscoveryPacket(itemName, itemCount);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(itemName);
        buf.writeInt(itemCount);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                ResourceLocation itemResourceLocation = new ResourceLocation(itemName);
                ItemStack itemStack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResourceLocation)), itemCount);
                int playerItemCount = player.getInventory().countItem(itemStack.getItem());
                if (playerItemCount >= itemCount) {
                    player.getInventory().clearOrCountMatchingItems(p -> p.getItem() == itemStack.getItem(), itemCount, player.inventoryMenu.getCraftSlots());
                    player.inventoryMenu.broadcastChanges();
                    EncyclopediaManager.getInstance().discoverItem(player.getUUID(), itemStack.getItem());
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}