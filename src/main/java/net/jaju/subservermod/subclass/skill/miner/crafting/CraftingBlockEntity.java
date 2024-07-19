package net.jaju.subservermod.subclass.skill.miner.crafting;

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

import java.util.Random;

public class CraftingBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public boolean flag = false;
    public static Random random = new Random();
    private int updateTimer = 0;
    private static final int UPDATE_INTERVAL = 20 * 10;

    public CraftingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRAFTING_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Crafting");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new CraftingContainer(id, playerInventory, this.getBlockPos());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CraftingBlockEntity entity) {
        if (!level.isClientSide) {
            if (entity.flag) entity.updateResultSlot();
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void updateResultSlot() {
        for (int i = 0; i < 9; i++) {
            if (itemHandler.getStackInSlot(9).getItem() == ModItem.CRAFTING_TOOL.get()) {
                itemHandler.setStackInSlot(i, crafting(itemHandler.getStackInSlot(i)));
            } else break;
        }
        flag = false;
    }

    private ItemStack crafting(ItemStack itemstack) {
        ItemStack returnItemStack;
        float rangeOfFailure = 30.0f;
        float rangeOfSuccess = 90.0f;
        float rangeOfHugeSuccess = 100.0f;

        float randomNumber = random.nextFloat(rangeOfHugeSuccess);

        if (itemstack.getItem() == Items.GOLD_INGOT) {
            if (randomNumber < rangeOfFailure) returnItemStack = ItemStack.EMPTY;
            else if (randomNumber < rangeOfSuccess) returnItemStack = new ItemStack(ModItem.GOLD_RING.get(), itemstack.getCount());
            else returnItemStack = new ItemStack(ModItem.GOLD_RING.get(), itemstack.getCount() * 2);

        } else if (itemstack.getItem() == Items.DIAMOND){
            if (randomNumber < rangeOfFailure) returnItemStack = ItemStack.EMPTY;
            else if (randomNumber < rangeOfSuccess) returnItemStack = new ItemStack(ModItem.DIAMOND_CUBIC.get(), itemstack.getCount());
            else returnItemStack = new ItemStack(ModItem.DIAMOND_CUBIC.get(), itemstack.getCount() * 2);

        } else if (itemstack.getItem() == Items.EMERALD){
            if (randomNumber < rangeOfFailure) returnItemStack = ItemStack.EMPTY;
            else if (randomNumber < rangeOfSuccess) returnItemStack = new ItemStack(ModItem.EMERALD_CUBIC.get(), itemstack.getCount());
            else returnItemStack = new ItemStack(ModItem.EMERALD_CUBIC.get(), itemstack.getCount() * 2);

        } else if (itemstack.getItem() == Items.LAPIS_BLOCK){
            if (randomNumber < rangeOfFailure) returnItemStack = ItemStack.EMPTY;
            else if (randomNumber < rangeOfSuccess) returnItemStack = new ItemStack(ModItem.LAPIS_LAZULI_MARBLE.get(), itemstack.getCount());
            else returnItemStack = new ItemStack(ModItem.LAPIS_LAZULI_MARBLE.get(), itemstack.getCount() * 2);

        } else if (itemstack.getItem() == Items.REDSTONE_BLOCK){
            if (randomNumber < rangeOfFailure) returnItemStack = ItemStack.EMPTY;
            else if (randomNumber < rangeOfSuccess) returnItemStack = new ItemStack(ModItem.REDSTONE_MARBLE.get(), itemstack.getCount());
            else returnItemStack = new ItemStack(ModItem.REDSTONE_MARBLE.get(), itemstack.getCount() * 2);

        } else {
            return itemstack;
        }


        ItemStack toolStack = itemHandler.getStackInSlot(9);
        toolStack.setDamageValue(toolStack.getDamageValue() + 1);
        if (toolStack.getDamageValue() >= toolStack.getMaxDamage()) {
            itemHandler.setStackInSlot(9, ItemStack.EMPTY);
        }


        return returnItemStack;

    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.flag = tag.getBoolean("Flag"); // flag 로드
        this.updateTimer = tag.getInt("UpdateTimer");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putBoolean("Flag", this.flag); // flag 저장
        tag.putInt("UpdateTimer", this.updateTimer);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
