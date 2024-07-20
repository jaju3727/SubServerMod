package net.jaju.subservermod.subclass.skill.farmer.oven;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.item.ModItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class OvenBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int updateTimer = 0;
    private static final int UPDATE_INTERVAL = 20 * 8;

    public OvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Oven");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new OvenContainer(id, playerInventory, this.getBlockPos());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OvenBlockEntity entity) {
        if (!level.isClientSide) {
            entity.updateResultSlot(
                    ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                    ItemStack.EMPTY, new ItemStack(ModItem.BUTTER.get(), 1), ItemStack.EMPTY,
                    ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                    new ItemStack(ModItem.CROISSANT.get(), 1)
            );

        }
    }

    private void updateResultSlot(ItemStack... ingredients) {
        List<ItemStack> ingredientList = Arrays.asList(ingredients);
        int minItemNum = Integer.MAX_VALUE;
        int temp;
        for (int i = 0; i < 9; i++) {
            if (itemHandler.getStackInSlot(i).getItem() != ingredientList.get(i).getItem()) {
                return;
            }
            temp = itemHandler.getStackInSlot(i).getCount();
            if (minItemNum > temp) {
                minItemNum = temp;
            }

        }
        ItemStack slot9 = itemHandler.getStackInSlot(9);
        ItemStack ingedient9 = ingredientList.get(9);
        ItemStack itemstack;

        if (slot9.isEmpty()) {
            itemstack = ingedient9.copy();
        } else if (slot9.getItem() == ingedient9.getItem()) {
            itemstack = new ItemStack(ingedient9.getItem(), slot9.getCount() + ingedient9.getCount());
        } else {
            return;
        }

        updateTimer++;
        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0;
        } else {
            return;
        }

        itemHandler.setStackInSlot(9, itemstack);


        for (int i = 0; i < 9; i++) {
            ItemStack slotStack = itemHandler.getStackInSlot(i);
            if (!slotStack.isEmpty()) {
                if (slotStack.getItem() == Items.MILK_BUCKET) {
                    ItemStack itemStack;
                    itemStack = new ItemStack(Items.BUCKET, minItemNum);
                    itemHandler.setStackInSlot(i, itemStack);
                    continue;
                }
                slotStack.shrink(minItemNum);
                itemHandler.setStackInSlot(i, slotStack);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.updateTimer = tag.getInt("UpdateTimer");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("UpdateTimer", this.updateTimer);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
