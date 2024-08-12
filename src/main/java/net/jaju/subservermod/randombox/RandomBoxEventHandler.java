package net.jaju.subservermod.randombox;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.shopsystem.network.UpdateInventoryPacket;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.jaju.subservermod.Subservermod;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import static net.jaju.subservermod.randombox.RandomBoxCommand.loadRandomBoxes;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class RandomBoxEventHandler {

    @SubscribeEvent
    public static void onPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
        if (EffectiveSide.get() != LogicalSide.SERVER) {
            return;
        }
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();

        if (itemStack.getItem() == ModItem.RANDOMBOX.get()) {
            loadRandomBoxes();

            if (itemStack.hasTag() && itemStack.getTag().contains("display")) {
                ListTag loreList = itemStack.getTag().getCompound("display").getList("Lore", 8); // StringTag type is 8
                if (!loreList.isEmpty()) {
                    String loreName = loreList.getString(0).replace("{\"text\":\"", "").replace("\"}","");
                    RandomBox randomBox = RandomBoxCommand.getRandomBox(loreName);

                    if (randomBox != null) {
                        ItemStack reward = randomBox.getRandomItem();
                        if (!reward.isEmpty()) {
                            player.sendSystemMessage(Component.literal("축하합니다! " + reward.getDisplayName().getString() + "를 얻었습니다."));

                            if (addItemsToInventory(player, reward) > 0) {
                                removeItemsFromInventory(player, itemStack);
                                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                                    ItemStack stack = player.getInventory().getItem(i);
                                    player.getInventory().setItem(i, stack);
                                }
                            } else {
                                player.sendSystemMessage(Component.literal("인벤토리를 비워주세요."));
                                return;
                            }

                            event.setCancellationResult(InteractionResult.SUCCESS);
                            event.setCanceled(true);
                        } else {
                            player.sendSystemMessage(Component.literal("랜덤 박스에서 아이템을 얻을 수 없습니다."));
                        }
                    }
                }
            }
        }
    }

    private static int addItemsToInventory(Player player, ItemStack itemStack) {
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

    private static int removeItemsFromInventory(Player player, ItemStack itemStack) {
        int remainingCount = 1;
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
}