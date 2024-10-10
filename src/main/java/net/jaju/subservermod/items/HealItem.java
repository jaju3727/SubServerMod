package net.jaju.subservermod.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
//            SoundPlayer.playCustomSound(player, new ResourceLocation(Subservermod.MOD_ID, "drink_potion_sound"), 8.0f, 8.0f);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
