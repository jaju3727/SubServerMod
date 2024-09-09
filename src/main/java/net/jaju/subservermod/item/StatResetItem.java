package net.jaju.subservermod.item;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.integrated_menu.network.TemporaryOpRequestPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StatResetItem extends Item {

    public StatResetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            if (player != null) {
                ModNetworking.INSTANCE.sendToServer(new TemporaryOpRequestPacket(
                        "/mmocore admin attr-realloc-points give " + player.getName().getString() +" 1"));

                ItemStack stack = player.getItemInHand(hand);
                if (!player.isCreative()) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.getInventory().removeItem(stack);
                    }
                }
            }
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("우클릭 시 스탯 초기화 포인트가 오릅니다."));
    }
}
