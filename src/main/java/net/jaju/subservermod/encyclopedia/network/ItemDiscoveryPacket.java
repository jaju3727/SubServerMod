package net.jaju.subservermod.encyclopedia.network;

import net.jaju.subservermod.encyclopedia.EncyclopediaManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
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
                int itemCountToRemove = itemCount;
                ResourceLocation itemResourceLocation = new ResourceLocation(itemName);
                Item item = ForgeRegistries.ITEMS.getValue(itemResourceLocation);
                ItemStack itemStack = new ItemStack(Objects.requireNonNull(item));
                if (itemResourceLocation.toString().contains("goat_horn")) {
                    itemStack = new ItemStack(Items.GOAT_HORN);
                    CompoundTag tag = itemStack.getOrCreateTag();
                    tag.putString("instrument", itemResourceLocation.toString());
                    itemStack.setTag(tag);
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack inventoryStack = player.getInventory().getItem(i);
                        if (ItemStack.isSameItemSameTags(inventoryStack, itemStack)) {
                            if (inventoryStack.getCount() <= itemCountToRemove) {
                                itemCountToRemove -= inventoryStack.getCount();
                                player.getInventory().removeItem(inventoryStack);
                            } else {
                                player.getInventory().removeItem(i, itemCountToRemove);
                                itemCountToRemove = 0;
                            }
                            if (itemCountToRemove <= 0) break;
                        }
                    }
                } else if (itemResourceLocation.toString().contains("potion")) {
                    String key = itemResourceLocation.toString();
                    String potionName = key.substring(key.indexOf(':') + 1).replace("_potion", "");
                    Potion potionType = Potion.byName(potionName);
                    if (potionType != null) {
                        itemStack = PotionUtils.setPotion(new ItemStack(Items.POTION), potionType);
                    }
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack inventoryStack = player.getInventory().getItem(i);
                        if (ItemStack.isSameItemSameTags(inventoryStack, itemStack)) {
                            if (inventoryStack.getCount() <= itemCountToRemove) {
                                itemCountToRemove -= inventoryStack.getCount();
                                player.getInventory().removeItem(inventoryStack);
                            } else {
                                player.getInventory().removeItem(i, itemCountToRemove);
                                itemCountToRemove = 0;
                            }
                            if (itemCountToRemove <= 0) break;
                        }
                    }
                } else {
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack inventoryStack = player.getInventory().getItem(i);
                        if (inventoryStack.getItem() == itemStack.getItem()) {
                            if (inventoryStack.getCount() <= itemCountToRemove) {
                                itemCountToRemove -= inventoryStack.getCount();
                                player.getInventory().removeItem(inventoryStack);
                            } else {
                                player.getInventory().removeItem(i, itemCountToRemove);
                                itemCountToRemove = 0;
                            }
                            if (itemCountToRemove <= 0) break;
                        }
                    }
                }

                player.inventoryMenu.broadcastChanges();
                EncyclopediaManager.getInstance().discoverItem(player.getUUID(), itemName);
            }
        });
        context.get().setPacketHandled(true);
    }
}