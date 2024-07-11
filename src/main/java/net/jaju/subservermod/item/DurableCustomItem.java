package net.jaju.subservermod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DurableCustomItem  extends Item {
    private static int maxDamage;
    private static int maxStackSize;

    public DurableCustomItem(Properties properties, int maxDamage, int maxStackSize) {
        super(properties);
        this.maxDamage = maxDamage;
        this.maxStackSize = maxStackSize;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false; // 아이템이 수리 가능한지 여부
    }

    @Override
    public boolean canBeDepleted() {
        return true; // 아이템이 소모될 수 있는지 여부
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return maxDamage; // 아이템의 최대 내구도 설정
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return maxStackSize; // 겹쳐질 수 있는 최대 아이템 수 설정
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, net.minecraft.world.entity.player.Player player) {
        super.onCraftedBy(stack, level, player);
        stack.setDamageValue(0);
    }
}
