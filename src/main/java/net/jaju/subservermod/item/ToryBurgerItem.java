package net.jaju.subservermod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToryBurgerItem extends Item {
    private final String customName;
    private final String lore;

    public ToryBurgerItem(Properties properties, String customName, String lore) {
        super(properties);
        this.customName = customName;
        this.lore = lore;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer) {
                if (player.getHealth() < player.getMaxHealth()) { // 체력이 가득 차 있지 않을 때만 사용 가능
                    player.startUsingItem(hand);
                    player.displayClientMessage(Component.literal("직접 방문시 희원 or 희성 이름 대면 치즈스틱 서비스를 드려요~"), true);
                    return InteractionResultHolder.consume(player.getItemInHand(hand));
                } else {
                    return InteractionResultHolder.fail(player.getItemInHand(hand));
                }
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(this.customName);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(lore));
    }
}
