package net.jaju.subservermod.events;

import net.jaju.subservermod.item.ModItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.jaju.subservermod.Subservermod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class PlayerDeathEventHandler {
    private static final Map<UUID, ListTag> playerInventories = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (hasSpecialItem(player)) {
                event.setCanceled(true);
                ListTag inventoryList = new ListTag();
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (!stack.isEmpty()) {
                        CompoundTag itemTag = new CompoundTag();
                        itemTag.putInt("Slot", i);
                        itemTag.put("Item", stack.save(new CompoundTag()));
                        inventoryList.add(itemTag);
                    }
                }
                playerInventories.put(player.getUUID(), inventoryList);
                player.sendSystemMessage(Component.literal("인벤토리 보호권이 사용되었습니다."));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUUID();
        if (playerInventories.containsKey(playerUUID)) {
            ListTag inventoryList = playerInventories.get(playerUUID);
            for (Tag tag : inventoryList) {
                CompoundTag itemTag = (CompoundTag) tag;
                int slot = itemTag.getInt("Slot");
                ItemStack stack = ItemStack.of(itemTag.getCompound("Item"));
                player.getInventory().setItem(slot, stack);
            }
            playerInventories.remove(playerUUID);
        }
    }

    private static boolean hasSpecialItem(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem().equals(ModItem.INVENTORY_SAVE_ITEM.get())) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.getInventory().removeItem(i, 1);
                }
                return true;
            }
        }
        return false;
    }
}
