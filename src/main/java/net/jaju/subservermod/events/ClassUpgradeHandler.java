package net.jaju.subservermod.events;

import net.jaju.subservermod.manager.MMOClassManager;
import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.manager.CoinManager;
import net.jaju.subservermod.network.coinsystem.CoinDataUpdatePacket;
import net.jaju.subservermod.entity.PlayerEntity;
import net.jaju.subservermod.network.integrated_menu.TemporaryOpRequestPacket;
import net.jaju.subservermod.network.mmoclass.packet.SyncClassDataPacket;
import net.jaju.subservermod.network.shopsystem.UpdateInventoryPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.jaju.subservermod.Subservermod;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class ClassUpgradeHandler {
    private static int tickCounter = 0;
    private static boolean isHandled = false;

    @SubscribeEvent
    public static void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide) {
            if (isHandled) {
                isHandled = false;
                return;
            }
            isHandled = true;
            Entity targetEntity = event.getTarget();
            Player player = event.getEntity();

            if (targetEntity instanceof PlayerEntity playerEntity) {
                String entityName = playerEntity.getName().getString();
                switch (entityName) {
                    case "마법사":
                        upgradePlayerLevel(player, "Mage");
                        break;
                    case "궁수":
                        upgradePlayerLevel(player, "Archer");
                        break;
                    case "전사":
                        upgradePlayerLevel(player, "Warrior");
                        break;
                    case "암살자":
                        upgradePlayerLevel(player, "Assassin");
                        break;
                    case "성직자":
                        upgradePlayerLevel(player, "Cleric");
                        break;
                }
            }
        }
    }

    private static void upgradePlayerLevel(Player player, String currentClass) {
        MMOClassManager classManager = MMOClassManager.getInstance();
        int currentLevel = classManager.getPlayerLevel(player.getUUID());
        String playerClass = classManager.getPlayerClass(player.getUUID());

        String weaponName = switch (currentClass) {
            case "Mage" -> "Staff of Mage";
            case "Archer" -> "Bow of Archer";
            case "Warrior" -> "Sword of Warrior";
            case "Assassin" -> "Blade of Assassin";
            case "Cleric" -> "Mace of Cleric";
            default -> "none";
        };



        if (playerClass.equals(currentClass) || playerClass.equals("None")) {
            CoinData coinData = CoinManager.getCoinData(player.getUUID());

            if (currentLevel == 5) {
                player.sendSystemMessage(Component.literal("최대 레벨에 도달했습니다."));
                return;
            }

            int amount = switch (currentLevel) {
                case 0 -> 1000;
                case 1 -> 3000;
                case 2 -> 6000;
                case 3 -> 10000;
                case 4 -> 15000;
                default -> 0;
            };

            if (!hasEnoughCoins(amount, coinData)) {
                player.sendSystemMessage(Component.literal(amount+"섭 코인이 필요합니다."));
                return;
            }

            if (currentLevel > 0 && !hasClassWeapon(player, weaponName, currentLevel, true)) {
                player.sendSystemMessage(Component.literal("직업 아이템을 가지고 전직을 진행해주세요."));
                return;
            }

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                ModNetworking.INSTANCE.sendToServer(new UpdateInventoryPacket(i, stack, player.getUUID()));
            }

            coinData.setSubcoin(Math.max(coinData.getSubcoin() - amount, 0));
            ModNetworking.INSTANCE.sendToServer(new CoinDataUpdatePacket(coinData, player.getUUID()));
            int nextClassLevel = currentLevel + 1;
            ModNetworking.INSTANCE.sendToServer(new TemporaryOpRequestPacket("/mi give SWORD " + currentClass + nextClassLevel));
            classManager.setPlayerClass(player.getUUID(), currentClass);
            classManager.setPlayerLevel(player.getUUID(), currentLevel + 1);


            ModNetworking.INSTANCE.sendToServer(new SyncClassDataPacket(player.getUUID(), currentClass, nextClassLevel));

            player.sendSystemMessage(Component.literal("축하합니다! " + currentClass + "의 레벨이 " + nextClassLevel + "로 증가했습니다."));
        } else {
            player.sendSystemMessage(Component.literal("현재 이 직업을 가지고 있지 않습니다."));
        }
    }

    private static boolean hasClassWeapon(Player player, String weaponName, int currentLevel, boolean itemRemove) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            String name = stack.getHoverName().getString();
            if (stack.getItem().equals(Items.STONE_SWORD) && stack.hasCustomHoverName() && name.contains(weaponName + " " + currentLevel)) {
                if (itemRemove) player.getInventory().removeItem(stack);
                return true;
            }
        }
        return false;
    }

    private static boolean hasEnoughCoins(int amount, CoinData coinData) {
        return coinData.getSubcoin() >= amount;
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItem().getItem();
        String name = stack.getHoverName().getString();
        if (stack.getItem().equals(Items.STONE_SWORD) && stack.hasCustomHoverName() &&
                (name.contains("Bow of Archer") ||
                        name.contains("Mace of Cleric") ||
                        name.contains("Blade of Assassin") ||
                        name.contains("Staff of Mage") ||
                        name.contains("Sword of Warrior"))) {
            MMOClassManager classManager = MMOClassManager.getInstance();
            String playerClass = classManager.getPlayerClass(player.getUUID());
            if (playerClass == null) return;
            int currentLevel = classManager.getPlayerLevel(player.getUUID());
            String weaponName = switch (playerClass) {
                case "Mage" -> "Staff of Mage";
                case "Archer" -> "Bow of Archer";
                case "Warrior" -> "Sword of Warrior";
                case "Assassin" -> "Blade of Assassin";
                case "Cleric" -> "Mace of Cleric";
                default -> "none";
            };

            if (!name.equals(weaponName + " " + currentLevel)) {
                player.sendSystemMessage(Component.literal("이 아이템을 얻을 수 없습니다!"));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack stack = event.getStack();
        String name = stack.getHoverName().getString();
        if (stack.getItem().equals(Items.STONE_SWORD) && stack.hasCustomHoverName() &&
                (name.contains("Bow of Archer") ||
                        name.contains("Mace of Cleric") ||
                        name.contains("Blade of Assassin") ||
                        name.contains("Staff of Mage") ||
                        name.contains("Sword of Warrior"))) {
            MMOClassManager classManager = MMOClassManager.getInstance();
            String playerClass = classManager.getPlayerClass(player.getUUID());
            if (playerClass == null) return;
            int currentLevel = classManager.getPlayerLevel(player.getUUID());
            String weaponName = switch (playerClass) {
                case "Mage" -> "Staff of Mage";
                case "Archer" -> "Bow of Archer";
                case "Warrior" -> "Sword of Warrior";
                case "Assassin" -> "Blade of Assassin";
                case "Cleric" -> "Mace of Cleric";
                default -> "none";
            };

            if (!name.equals(weaponName + " " + currentLevel)) {
                player.sendSystemMessage(Component.literal("이 아이템을 얻을 수 없습니다!"));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter < 20) return; // 1초(20틱)마다 실행
        tickCounter = 0;

        Player player = event.player;

        MMOClassManager classManager = MMOClassManager.getInstance();
        String playerClass = classManager.getPlayerClass(player.getUUID());
        if (playerClass == null) return;

        int currentLevel = classManager.getPlayerLevel(player.getUUID());
        String validWeaponName = switch (playerClass) {
            case "Mage" -> "Staff of Mage";
            case "Archer" -> "Bow of Archer";
            case "Warrior" -> "Sword of Warrior";
            case "Assassin" -> "Blade of Assassin";
            case "Cleric" -> "Mace of Cleric";
            default -> "none";
        };

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            String name = stack.getHoverName().getString();
            if (stack.getItem().equals(Items.STONE_SWORD) && stack.hasCustomHoverName() &&
                    (name.contains("Bow of Archer") ||
                            name.contains("Mace of Cleric") ||
                            name.contains("Blade of Assassin") ||
                            name.contains("Staff of Mage") ||
                            name.contains("Sword of Warrior"))) {

                if (!name.equals(validWeaponName + " " + currentLevel)) {
                    player.sendSystemMessage(Component.literal("이 아이템을 소유할 수 없습니다!"));

                    // 아이템을 바닥에 떨어뜨리기
                    ItemStack droppedItem = player.getInventory().removeItem(i, stack.getCount());
                    player.drop(droppedItem, false); // false: 던지기 애니메이션 없이 바로 떨어뜨림
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack stack = event.getLeft();
        String name = stack.getHoverName().getString();
        if (stack.getItem().equals(Items.STONE_SWORD) && stack.hasCustomHoverName() &&
                (name.contains("Bow of Archer") ||
                        name.contains("Mace of Cleric") ||
                        name.contains("Blade of Assassin") ||
                        name.contains("Staff of Mage") ||
                        name.contains("Sword of Warrior"))) {
            event.setCanceled(true);
        }
    }
}
