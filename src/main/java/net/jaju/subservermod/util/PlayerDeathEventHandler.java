package net.jaju.subservermod.util;

import net.jaju.subservermod.items.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Items;
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
    private static final Map<UUID, Integer> playerExperience = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (hasSpecialItem(player)) {
                playerExperience.put(player.getUUID(), player.experienceLevel);
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
            } else {
                ListTag inventoryList = new ListTag();
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    String name = stack.getHoverName().getString();
                    if (stack.getItem().equals(Items.STONE_SWORD) && stack.hasCustomHoverName() &&
                            (name.contains("Bow of Archer") ||
                            name.contains("Mace of Cleric") ||
                            name.contains("Blade of Assassin") ||
                            name.contains("Staff of Mage") ||
                            name.contains("Sword of Warrior"))) {
                        CompoundTag itemTag = new CompoundTag();
                        itemTag.putInt("Slot", i);
                        itemTag.put("Item", stack.save(new CompoundTag()));
                        inventoryList.add(itemTag);
                        player.getInventory().removeItem(stack);
                    }
                }

                if (!inventoryList.isEmpty()) {
                    playerInventories.put(player.getUUID(), inventoryList);
                }
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

        if (playerExperience.containsKey(playerUUID)) {
            int experience = playerExperience.get(playerUUID);
            player.giveExperienceLevels(experience);
            playerExperience.remove(playerUUID);
        }


    }

    /**
     * /mmocore admin attribute-points give {playerName} {num}
     * /mmocore admin attribute-points set {playerName} {num}

     * /mmocore admin attribute give {playerName} {stat} {num}
     * /mmocore admin attribute take {playerName} {stat} {num}
     * 스탯 목록:
     * skill_damage
     * cooldown_reduction
     * health_regeneration
     * armor
     * knockback_resistance
     * movement_speed
     * max_health
     */
    private static boolean hasSpecialItem(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem().equals(ModItems.INVENTORY_SAVE_ITEM.get())) {
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
