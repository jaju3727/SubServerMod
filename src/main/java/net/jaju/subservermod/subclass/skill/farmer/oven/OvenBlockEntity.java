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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OvenBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int updateTimer = 0;
    private static final int UPDATE_INTERVAL = 20 * 6;
    private static final LinkedHashMap<String, List<ItemStack>> recipes = new LinkedHashMap<>();

    static {
        recipes.put("Croissant", List.of(
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(ModItem.BUTTER.get(), 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                new ItemStack(ModItem.CROISSANT.get(), 1)
        ));
        recipes.put("Baguette", List.of(
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(Items.WHEAT, 1), ItemStack.EMPTY,
                new ItemStack(ModItem.BAGUETTE.get(), 1)
        ));
        recipes.put("Chocloate", List.of(
                new ItemStack(Items.COCOA_BEANS, 1), new ItemStack(Items.COCOA_BEANS, 1), new ItemStack(Items.COCOA_BEANS, 1),
                new ItemStack(Items.SUGAR, 1), new ItemStack(Items.SUGAR, 1), new ItemStack(Items.SUGAR, 1),
                ItemStack.EMPTY, new ItemStack(ModItem.BUTTER.get(), 1), ItemStack.EMPTY,
                new ItemStack(ModItem.CHOCOLATE.get(), 1)
        ));
        recipes.put("Brownie", List.of(
                ItemStack.EMPTY, new ItemStack(Items.EGG, 1), ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(ModItem.CHOCOLATE.get(), 1), ItemStack.EMPTY,
                new ItemStack(Items.WHEAT, 1), new ItemStack(Items.WHEAT, 1), new ItemStack(Items.WHEAT, 1),
                new ItemStack(ModItem.BROWNIE.get(), 1)
        ));
    }

    public void updateRecipeName(String recipeName, Player player) {
        if (recipes.containsKey(recipeName) && player != null) {
            List<ItemStack> ingredientList = recipes.get(recipeName);
            Inventory playerInventory = player.getInventory();
            HashMap<Item, Integer> playerItems = new HashMap<>();
            HashMap<Item, Integer> ingredientItems = new HashMap<>();
            int craftableAmount = Integer.MAX_VALUE;

            for (int i = 0; i < 9; i++) {
                ItemStack playerStack = itemHandler.getStackInSlot(i);
                playerInventory.add(playerStack);
                itemHandler.setStackInSlot(i, new ItemStack(Items.AIR));
            }

            for (int i = 0; i < playerInventory.getContainerSize(); i++) {
                ItemStack playerStack = playerInventory.getItem(i);
                Item playerItem = playerStack.getItem();
                playerItems.put(playerItem, playerItems.getOrDefault(playerItem, 0) + playerStack.getCount());
            }

            // 재료 리스트의 각 재료 개수 계산
            for (int i = 0; i < ingredientList.size() - 1; i++) {
                ItemStack itemStack = ingredientList.get(i);
                Item ingredientItem = itemStack.getItem();
                if (!itemStack.isEmpty()) {
                    ingredientItems.put(ingredientItem, ingredientItems.getOrDefault(ingredientItem, 0) + itemStack.getCount());
                }
            }

            for (var entry : ingredientItems.entrySet()) {
                craftableAmount = Math.min(craftableAmount, playerItems.getOrDefault(entry.getKey(), 0) / entry.getValue());
            }

            craftableAmount = Math.min(craftableAmount, 6);

            for (int i = 0; i < ingredientList.size() - 1; i++) {
                if (!ingredientList.get(i).isEmpty()) {
                    itemHandler.setStackInSlot(i, new ItemStack(ingredientList.get(i).getItem(), craftableAmount));
                }
            }

            for (var entry : ingredientItems.entrySet()) {
                int amountToRemove = craftableAmount * entry.getValue();
                for (int i = 0; i < playerInventory.getContainerSize() && amountToRemove > 0; i++) {
                    ItemStack playerStack = playerInventory.getItem(i);
                    if (playerStack.getItem() == entry.getKey()) {
                        int removeCount = Math.min(playerStack.getCount(), amountToRemove);
                        playerStack.shrink(removeCount);
                        amountToRemove -= removeCount;
                    }
                }
            }
        }
    }

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
            for (var entry : recipes.entrySet()) {
                entity.updateResultSlot(entry.getValue());
            }
        }
    }

    private void updateResultSlot(List<ItemStack> ingredientList) {
        int minItemNum = Integer.MAX_VALUE;
        int temp;
        for (int i = 0; i < 9; i++) {
            if (itemHandler.getStackInSlot(i).getItem() != ingredientList.get(i).getItem()) {
                return;
            }
            if (!itemHandler.getStackInSlot(i).isEmpty()) temp = itemHandler.getStackInSlot(i).getCount();
            else continue;
            if (minItemNum > temp) {
                minItemNum = temp;
            }
        }
        ItemStack slot9 = itemHandler.getStackInSlot(9);
        ItemStack ingedient9 = ingredientList.get(9);
        ItemStack itemstack;

        if (slot9.isEmpty()) {
            itemstack = new ItemStack(ingedient9.getItem(), ingedient9.getCount() * minItemNum);
        } else if (slot9.getItem() == ingedient9.getItem()) {
            itemstack = new ItemStack(ingedient9.getItem(), slot9.getCount() + ingedient9.getCount() * minItemNum);
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
