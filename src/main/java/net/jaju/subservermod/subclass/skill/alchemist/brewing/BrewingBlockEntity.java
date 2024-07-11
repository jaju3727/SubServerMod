package net.jaju.subservermod.subclass.skill.alchemist.brewing;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.util.BrewingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public class BrewingBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(8) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int blazeCount = 0;
    private int waterCount = 0;
    private int stage = 2;
    private int updateTimer = 0;
    private static final int UPDATE_INTERVAL = 20 * 7;

    public BrewingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BREWING_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Brewing");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new BrewingContainer(id, playerInventory, this.getBlockPos());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BrewingBlockEntity entity) {
        if (!level.isClientSide) {
            entity.updateResultSlot();
        }
    }

    private void updateResultSlot() {
        if (itemHandler.getStackInSlot(0).getItem() == Items.BLAZE_POWDER && blazeCount == 0) {
            blazeCount = 20;
            itemHandler.setStackInSlot(0, new ItemStack(itemHandler.getStackInSlot(0).getItem(), itemHandler.getStackInSlot(0).getCount() - 1));
        }
        if (itemHandler.getStackInSlot(1).getItem() == Items.WATER_BUCKET && waterCount == 0) {
            waterCount = 16;
            itemHandler.setStackInSlot(1, new ItemStack(Items.BUCKET, 1));
        }
        if (waterCount != 0 && itemHandler.getStackInSlot(5).getItem() == Items.GLASS_BOTTLE && itemHandler.getStackInSlot(6).isEmpty()) {
            waterCount--;
            itemHandler.setStackInSlot(5, new ItemStack(itemHandler.getStackInSlot(5).getItem(), itemHandler.getStackInSlot(5).getCount() - 1));
            itemHandler.setStackInSlot(6, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
            stage = 2;
            updateTimer = 0;
        }
        if (!itemHandler.getStackInSlot(6).isEmpty()) {
            if (UPDATE_INTERVAL < updateTimer) {
                ItemStack result = BrewingHelper.getPotionResult(itemHandler.getStackInSlot(6), new ItemStack(itemHandler.getStackInSlot(stage).getItem()));
                if (!result.isEmpty()) {
                    if (stage == 4 || itemHandler.getStackInSlot(stage + 1).isEmpty()) {
                        if (itemHandler.getStackInSlot(7).isEmpty()) {
                            itemHandler.setStackInSlot(7, result);
                            itemHandler.setStackInSlot(6, ItemStack.EMPTY);
                            stage = 2;
                            updateTimer = 0;
                            return;
                        }
                        else if (ItemStack.isSameItem(itemHandler.getStackInSlot(7), result) && ItemStack.isSameItemSameTags(itemHandler.getStackInSlot(7), result)) {
                            itemHandler.setStackInSlot(7, PotionUtils.setPotion(new ItemStack(Items.POTION, itemHandler.getStackInSlot(7).getCount() + 1),  PotionUtils.getPotion(result)));
                            itemHandler.setStackInSlot(6, ItemStack.EMPTY);
                            stage = 2;
                            updateTimer = 0;
                            return;
                        }

                    }
                    itemHandler.setStackInSlot(6, result);
                    itemHandler.setStackInSlot(stage, new ItemStack(itemHandler.getStackInSlot(stage).getItem(), itemHandler.getStackInSlot(stage).getCount() -1));
                    stage++;
                    updateTimer = 0;
                }
            } else {
                updateTimer++;
            }
        }

    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("brewingInventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("brewingInventory", itemHandler.serializeNBT());
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
