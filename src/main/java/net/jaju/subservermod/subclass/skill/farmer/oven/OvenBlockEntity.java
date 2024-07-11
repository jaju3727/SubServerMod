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
    private static final int UPDATE_INTERVAL = 20 * 10;

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
                    new ItemStack(ModItem.CROISSANT.get(), 3)
            );

            entity.updateResultSlot(
                    ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                    new ItemStack(Items.WHEAT, 1), new ItemStack(Items.WHEAT, 1), new ItemStack(Items.WHEAT, 1),
                    ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                    new ItemStack(Items.BREAD, 9)
            );

            entity.updateResultSlot(
                    ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                    new ItemStack(Items.WHEAT, 1), new ItemStack(Items.COCOA_BEANS, 1), new ItemStack(Items.WHEAT, 1),
                    ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                    new ItemStack(Items.COOKIE, 24)
            );

            entity.updateResultSlot(
                    ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                    new ItemStack(Items.PUMPKIN, 1), new ItemStack(Items.SUGAR, 1), ItemStack.EMPTY,
                    ItemStack.EMPTY, new ItemStack(Items.EGG, 1), ItemStack.EMPTY,
                    new ItemStack(Items.PUMPKIN_PIE, 3)
            );

            entity.updateResultSlot(
                    new ItemStack(Items.MILK_BUCKET, 1), new ItemStack(Items.MILK_BUCKET, 1), new ItemStack(Items.MILK_BUCKET, 1),
                    new ItemStack(Items.SUGAR, 1), new ItemStack(Items.EGG, 1), new ItemStack(Items.SUGAR, 1),
                    new ItemStack(Items.WHEAT, 1), new ItemStack(Items.WHEAT, 1), new ItemStack(Items.WHEAT, 1),
                    new ItemStack(Items.CAKE, 3)
            );

        }
    }

    private void updateResultSlot(ItemStack... ingredients) {
        List<ItemStack> ingredientList = Arrays.asList(ingredients);
        for (int i = 0; i < 9; i++) {
            if (itemHandler.getStackInSlot(i).getItem() != ingredientList.get(i).getItem()) {
                return;
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
                    if (itemHandler.getStackInSlot(10).isEmpty()) itemStack = new ItemStack(Items.BUCKET, 1);
                    else if(itemHandler.getStackInSlot(10).getItem() == Items.BUCKET) itemStack = new ItemStack(Items.BUCKET, itemHandler.getStackInSlot(10).getCount() + 1);
                    else itemStack = itemHandler.getStackInSlot(10);

                    itemHandler.setStackInSlot(10, itemStack);
                }
                slotStack.shrink(1);
                itemHandler.setStackInSlot(i, slotStack);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
