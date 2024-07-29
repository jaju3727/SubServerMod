package net.jaju.subservermod.subclass.skill.farmer.middle_oven;

import net.minecraft.world.item.ItemStack;
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
}
