package net.jaju.subservermod.randombox;

import net.jaju.subservermod.item.ModItem;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.jaju.subservermod.Subservermod;
import net.minecraftforge.fml.util.thread.EffectiveSide;

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
            if (itemStack.hasTag() && itemStack.getTag().contains("display")) {
                ListTag loreList = itemStack.getTag().getCompound("display").getList("Lore", 8); // StringTag type is 8
                if (!loreList.isEmpty()) {
                    String loreName = loreList.getString(0).replace("{\"text\":\"", "").replace("\"}","");
                    RandomBox randomBox = RandomBoxCommand.getRandomBox(loreName);

                    if (randomBox != null) {
                        ItemStack reward = randomBox.getRandomItem();
                        if (!reward.isEmpty()) {
                            player.sendSystemMessage(Component.literal("축하합니다! " + reward.getDisplayName().getString() + "를 얻었습니다."));
                            if (!player.getInventory().add(reward)) {
                                player.drop(reward, false);
                            }
                            itemStack.shrink(1);
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
}