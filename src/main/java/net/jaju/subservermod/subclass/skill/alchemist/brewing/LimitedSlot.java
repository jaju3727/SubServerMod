package net.jaju.subservermod.subclass.skill.alchemist.brewing;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class LimitedSlot extends SlotItemHandler {
    private final int maxStackSize;

    public LimitedSlot(ItemStackHandler itemHandler, int index, int xPosition, int yPosition, int maxStackSize) {
        super(itemHandler, index, xPosition, yPosition);
        this.maxStackSize = maxStackSize;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return maxStackSize;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack stackInSlot = this.getItem();

        if (stackInSlot.getItem() == Items.POTION && stackInSlot.getCount() > 1 && !player.containerMenu.getCarried().isEmpty()) {
            return false;
        }
        return super.mayPickup(player);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        ItemStack carriedItem = player.containerMenu.getCarried();

        if (!carriedItem.isEmpty() && stack.getItem() == Items.POTION && stack.getCount() > 1) {
            this.set(stack);
        } else {
            super.onTake(player, stack);
        }
    }
}
