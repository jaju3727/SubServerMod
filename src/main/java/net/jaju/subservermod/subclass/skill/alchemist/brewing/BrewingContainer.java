package net.jaju.subservermod.subclass.skill.alchemist.brewing;

import net.jaju.subservermod.ModContainers;
import net.jaju.subservermod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BrewingContainer extends AbstractContainerMenu {
    private final BrewingBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public BrewingContainer(int id, Inventory playerInventory, BlockPos pos) {
        super(ModContainers.BREWING_MENU.get(), id);
        this.blockEntity = (BrewingBlockEntity) playerInventory.player.level().getBlockEntity(pos);
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addBrewingSlots();
        addPlayerSlots(playerInventory);

    }

    private void addBrewingSlots() {
        ItemStackHandler itemHandler = blockEntity.getItemHandler();
        this.addSlot(new SlotItemHandler(itemHandler, 0, 30, 26)); //블레이즈 가루
        this.addSlot(new LimitedSlot(itemHandler, 1, 30, 50, 1)); //물 양동이
        this.addSlot(new SlotItemHandler(itemHandler, 2, 70, 30)); //네더와트
        this.addSlot(new SlotItemHandler(itemHandler, 3, 70, 48)); //재료1
        this.addSlot(new SlotItemHandler(itemHandler, 4, 70, 66)); //재료2
        this.addSlot(new SlotItemHandler(itemHandler, 5, 70, 84)); //재료3
        this.addSlot(new SlotItemHandler(itemHandler, 6, 50, 30)); //유리병
        this.addSlot(new LimitedSlot(itemHandler, 7, 50, 48, 0)); //변화병
        this.addSlot(new LimitedSlot(itemHandler, 8, 50, 66, 0)); //최종 병
    }

    private void addPlayerSlots(Inventory playerInventory) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 120 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 178));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.BREWING_BLOCK.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();

            if (index < 10) {
                if (!this.moveItemStackTo(stackInSlot, 10, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 10, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return stack;
    }

    public boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack slotStack = slot.getItem();

                if (!slotStack.isEmpty() && ItemStack.isSameItemSameTags(stack, slotStack)) {
                    int combinedCount = slotStack.getCount() + stack.getCount();
                    int maxStackSize = Math.min(slot.getMaxStackSize(slotStack), stack.getMaxStackSize());

                    if (combinedCount <= maxStackSize) {
                        stack.setCount(0);
                        slotStack.setCount(combinedCount);
                        slot.set(slotStack);
                        flag = true;
                    } else if (slotStack.getCount() < maxStackSize) {
                        stack.shrink(maxStackSize - slotStack.getCount());
                        slotStack.setCount(maxStackSize);
                        slot.set(slotStack);
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack slotStack = slot.getItem();

                if (slotStack.isEmpty() && slot.mayPlace(stack)) {
                    if (stack.getCount() > slot.getMaxStackSize(stack)) {
                        ItemStack newStack = stack.split(slot.getMaxStackSize(stack));
                        slot.set(newStack);
                    } else {
                        slot.set(stack.split(stack.getCount()));
                    }
                    slot.setChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }
    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (slot instanceof LimitedSlot) {
            return stack.getCount() <= (slot).getMaxStackSize();
        }
        return super.canTakeItemForPickAll(stack, slot);
    }
}