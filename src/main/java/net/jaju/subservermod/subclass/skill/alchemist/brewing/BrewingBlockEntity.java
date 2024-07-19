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
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class BrewingBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int blazeCount = 0;
    private int waterCount = 0;
    private int stage = 2;
    private int updateTimer = 0;
    private int UPDATE_INTERVAL = 20 * 7;

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
            blazeCount = 60;
            itemHandler.setStackInSlot(0, new ItemStack(itemHandler.getStackInSlot(0).getItem(), itemHandler.getStackInSlot(0).getCount() - 1));
        }
        if (itemHandler.getStackInSlot(1).getItem() == Items.WATER_BUCKET && waterCount == 0) {
            waterCount = 16;
            itemHandler.setStackInSlot(1, new ItemStack(Items.BUCKET, 1));
        }
        if (waterCount != 0 && itemHandler.getStackInSlot(6).getItem() == Items.GLASS_BOTTLE && itemHandler.getStackInSlot(7).isEmpty()) {
            waterCount--;
            itemHandler.setStackInSlot(6, new ItemStack(itemHandler.getStackInSlot(6).getItem(), itemHandler.getStackInSlot(6).getCount() - 1));
            itemHandler.setStackInSlot(7, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
            stage = 2;
            UPDATE_INTERVAL = 7 * 20;
            updateTimer = 0;
        }
        if (!itemHandler.getStackInSlot(7).isEmpty() && !itemHandler.getStackInSlot(stage).isEmpty()) {
            if (UPDATE_INTERVAL < updateTimer) {
                ItemStack result = BrewingHelper.getPotionResult(itemHandler.getStackInSlot(7), new ItemStack(itemHandler.getStackInSlot(stage).getItem()));
                if (!result.isEmpty()) {
                    if (stage == 5 || itemHandler.getStackInSlot(stage + 1).isEmpty()) {
                        if (itemHandler.getStackInSlot(8).isEmpty()) {
                            itemHandler.setStackInSlot(8, result);
                            itemHandler.setStackInSlot(7, ItemStack.EMPTY);
                            itemHandler.setStackInSlot(stage, new ItemStack(itemHandler.getStackInSlot(stage).getItem(), itemHandler.getStackInSlot(stage).getCount() -1));
                            blazeCount--;
                            return;
                        }
                        else if (ItemStack.isSameItem(itemHandler.getStackInSlot(8), result) && ItemStack.isSameItemSameTags(itemHandler.getStackInSlot(8), result)) {
                            itemHandler.setStackInSlot(8, PotionUtils.setPotion(new ItemStack(Items.POTION, itemHandler.getStackInSlot(8).getCount() + 1),  PotionUtils.getPotion(result)));
                            itemHandler.setStackInSlot(7, ItemStack.EMPTY);
                            itemHandler.setStackInSlot(stage, new ItemStack(itemHandler.getStackInSlot(stage).getItem(), itemHandler.getStackInSlot(stage).getCount() -1));
                            blazeCount--;
                            return;
                        }

                    }
                    itemHandler.setStackInSlot(7, result);
                    itemHandler.setStackInSlot(stage, new ItemStack(itemHandler.getStackInSlot(stage).getItem(), itemHandler.getStackInSlot(stage).getCount() -1));
                    stage++;
                    updateTimer = 0;
                    UPDATE_INTERVAL = ((stage-2)+7)*20;
                    blazeCount--;
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
        this.blazeCount = tag.getInt("BlazeCount");
        this.waterCount = tag.getInt("WaterCount");
        this.stage = tag.getInt("Stage");
        this.updateTimer = tag.getInt("UpdateTimer");
        this.UPDATE_INTERVAL = tag.getInt("UpdateInterval");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("brewingInventory", itemHandler.serializeNBT());
        tag.putInt("BlazeCount", this.blazeCount);
        tag.putInt("WaterCount", this.waterCount);
        tag.putInt("Stage", this.stage);
        tag.putInt("UpdateTimer", this.updateTimer);
        tag.putInt("UpdateInterval", this.UPDATE_INTERVAL);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
