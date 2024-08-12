package net.jaju.subservermod.player;

import com.google.gson.JsonArray;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.integrated_menu.network.TemporaryOpPacket;
import net.jaju.subservermod.util.ItemStackSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ManagerModConvert {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("변신")
                        .executes(ctx -> switchInventories(ctx.getSource()))
        );
    }

    private static int switchInventories(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (!PlayerManager.isPlayerAllowed(player)) {
            player.sendSystemMessage(Component.literal("You are not allowed to use this command."));
            return 1;
        }
        String playerName = player.getGameProfile().getName();

        if (InventoryManager.isUsingAdminInventory(playerName)) {
            switchToNormalInventory(player);
        } else {
            switchToAdminInventory(player);
        }
        return 1;
    }

    private static void switchToNormalInventory(ServerPlayer player) {
        String playerName = player.getGameProfile().getName();

        // 현재 인벤토리를 관리자의 인벤토리로 저장
        saveCurrentInventory(player, true);

        // 저장된 일반 인벤토리를 불러오기
        if (InventoryManager.hasNormalInventory(playerName)) {
            JsonArray normalInventoryJson = InventoryManager.getNormalInventory(playerName);
            ItemStack[] normalInventory = ItemStackSerializer.deserializeInventory(normalInventoryJson);

            // 인벤토리 스위칭
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                if (i < normalInventory.length) {
                    player.getInventory().items.set(i, normalInventory[i]);
                } else {
                    player.getInventory().items.set(i, ItemStack.EMPTY);
                }
            }
        }

        // 오피 권한 제거
        ModNetworking.INSTANCE.sendToServer(new TemporaryOpPacket(false));

        // 상태 업데이트
        InventoryManager.setUsingAdminInventory(playerName, false);
    }

    private static void switchToAdminInventory(ServerPlayer player) {
        String playerName = player.getGameProfile().getName();

        // 현재 인벤토리를 일반 인벤토리로 저장
        saveCurrentInventory(player, false);

        // 저장된 관리자의 인벤토리를 불러오기
        if (InventoryManager.hasAdminInventory(playerName)) {
            JsonArray adminInventoryJson = InventoryManager.getAdminInventory(playerName);
            ItemStack[] adminInventory = ItemStackSerializer.deserializeInventory(adminInventoryJson);

            // 인벤토리 스위칭
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                if (i < adminInventory.length) {
                    player.getInventory().items.set(i, adminInventory[i]);
                } else {
                    player.getInventory().items.set(i, ItemStack.EMPTY);
                }
            }
        } else {
            // 새로운 빈 인벤토리 생성
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                player.getInventory().items.set(i, ItemStack.EMPTY);
            }
        }

        // 오피 권한 부여
        ModNetworking.INSTANCE.sendToServer(new TemporaryOpPacket(true));

        // 상태 업데이트
        InventoryManager.setUsingAdminInventory(playerName, true);
    }

    private static void saveCurrentInventory(ServerPlayer player, boolean isAdmin) {
        ItemStack[] inventory = new ItemStack[player.getInventory().items.size()];
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            inventory[i] = player.getInventory().items.get(i).copy();
        }
        JsonArray jsonInventory = ItemStackSerializer.serializeInventory(inventory);
        String playerName = player.getGameProfile().getName();

        if (isAdmin) {
            InventoryManager.saveAdminInventory(playerName, jsonInventory);
        } else {
            InventoryManager.saveNormalInventory(playerName, jsonInventory);
        }
    }
}
