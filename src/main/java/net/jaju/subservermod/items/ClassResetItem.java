package net.jaju.subservermod.items;

import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.manager.MailboxManager;
import net.jaju.subservermod.network.mmoclass.packet.ClassDataSyncPacket;
import net.jaju.subservermod.manager.MMOClassManager;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ClassResetItem extends Item {
    public ClassResetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            if (player != null) {
                MMOClassManager classManager = MMOClassManager.getInstance();

                UUID playerUUID = player.getUUID();
                if (!hasClassWeapon(player, classManager.getPlayerClass(playerUUID), classManager.getPlayerLevel(playerUUID), true)) {
                    player.sendSystemMessage(Component.literal("직업 아이템을 가지고 초기화를 진행해주세요."));
                    return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
                }

                int cost = getCostBasedOnLevel(classManager.getPlayerLevel(playerUUID));
                classManager.setPlayerClass(playerUUID, "None");
                classManager.setPlayerLevel(playerUUID, 0);


                ModNetworking.INSTANCE.sendTo(
                        new ClassDataSyncPacket(playerUUID, classManager.getPlayerClass(playerUUID), classManager.getPlayerLevel(playerUUID)),
                        ((ServerPlayer) player).connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
                ItemStack stack = player.getItemInHand(hand);



                if (cost > 0) {
                    ItemStack coinItem = new ItemStack(ModItems.SUB_COIN.get());

                    MutableComponent lore = Component.literal((cost) + "원");
                    ListTag loreList = new ListTag();
                    loreList.add(StringTag.valueOf(Component.Serializer.toJson(lore)));

                    coinItem.getOrCreateTagElement("display").put("Lore", loreList);

                    MailboxManager.getInstance().addItemToMailbox(playerUUID, coinItem);
                }

                if (!player.isCreative()) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.getInventory().removeItem(stack);
                    }
                }

                player.sendSystemMessage(Component.literal("직업이 초기화 되었습니다."));
            }
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("우클릭 시 전투 직업이 초기화됩니다."));
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

    private int getCostBasedOnLevel(int playerLevel) {
        switch (playerLevel) {
            case 2:
                return 250;
            case 3:
                return 500;
            case 4:
                return 1000;
            case 5:
                return 2500;
            default:
                return 0;
        }
    }
}
