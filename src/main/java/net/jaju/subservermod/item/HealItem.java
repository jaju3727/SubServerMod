package net.jaju.subservermod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HealItem extends Item {
    private final int healNum;
    private final int coolTime;

    public HealItem(Properties properties, int healNum, int coolTime) {
        super(properties);
        this.healNum = healNum;
        this.coolTime = coolTime;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            player.heal(healNum);
            player.sendSystemMessage(Component.literal("체력이 회복되었습니다!"));
            itemStack.shrink(1);
            player.getCooldowns().addCooldown(this, coolTime * 20);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
