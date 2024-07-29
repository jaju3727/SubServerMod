package net.jaju.subservermod.subclass.skill.alchemist.brewing;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.subclass.network.BrewingVarSendToClientPacket;
import net.jaju.subservermod.subclass.network.GaugeSendToClientPacket;
import net.jaju.subservermod.util.BrewingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.ArrayList;
import java.util.List;

public class BrewingBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(13) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int blazeCount = 0;
    private int waterCount = 0;
    private List<Integer> stage = new ArrayList<>(List.of(2, 2, 2));
    private List<Integer> updateTimer = new ArrayList<>(List.of(0, 0, 0));
    private List<Integer> UPDATE_INTERVAL = new ArrayList<>(List.of(20 * 7, 20 * 7, 20 * 7));

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
        for (int i = 0; i <= 2; i++) {
            if (itemHandler.getStackInSlot(0).getItem() == Items.BLAZE_POWDER && blazeCount == 0) {
                blazeCount = 60;
                itemHandler.setStackInSlot(0, new ItemStack(itemHandler.getStackInSlot(0).getItem(), itemHandler.getStackInSlot(0).getCount() - 1));
            }
            if (itemHandler.getStackInSlot(1).getItem() == Items.WATER_BUCKET && waterCount == 0) {
                waterCount = 16;
                //$물소리
                itemHandler.setStackInSlot(1, new ItemStack(Items.BUCKET, 1));
            }
            if (waterCount != 0 && itemHandler.getStackInSlot(6).getItem() == Items.GLASS_BOTTLE && itemHandler.getStackInSlot(7+ i*2).isEmpty()) {
                waterCount--;
                itemHandler.setStackInSlot(6, new ItemStack(itemHandler.getStackInSlot(6).getItem(), itemHandler.getStackInSlot(6).getCount() - 1));
                itemHandler.setStackInSlot(7+ i*2, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                stage.set(i, 2);
                UPDATE_INTERVAL.set(i, 7 * 20);
                updateTimer.set(i, 0);
            }
            if (!itemHandler.getStackInSlot(7+ i*2).isEmpty() && !itemHandler.getStackInSlot(stage.get(i)).isEmpty() && blazeCount > 0) {
                if (UPDATE_INTERVAL.get(i) < updateTimer.get(i)) {
                    ItemStack result = BrewingHelper.getPotionResult(itemHandler.getStackInSlot(7+ i*2), new ItemStack(itemHandler.getStackInSlot(stage.get(i)).getItem()));
                    if (!result.isEmpty()) {
                        if (stage.get(i) == 5 || itemHandler.getStackInSlot(stage.get(i) + 1).isEmpty()) {
                            if (itemHandler.getStackInSlot(8+ i*2).isEmpty()) {
                                itemHandler.setStackInSlot(8+ i*2, result);
                                itemHandler.setStackInSlot(7+ i*2, ItemStack.EMPTY);
                                if (i == 0) itemHandler.setStackInSlot(stage.get(i), new ItemStack(itemHandler.getStackInSlot(stage.get(i)).getItem(), itemHandler.getStackInSlot(stage.get(i)).getCount() - 1));
                                blazeCount--;
                                return;
                            } else if (ItemStack.isSameItem(itemHandler.getStackInSlot(8+ i*2), result) && ItemStack.isSameItemSameTags(itemHandler.getStackInSlot(8+ i*2), result)) {
                                itemHandler.setStackInSlot(8+ i*2, PotionUtils.setPotion(new ItemStack(Items.POTION, itemHandler.getStackInSlot(8+ i*2).getCount() + 1), PotionUtils.getPotion(result)));
                                itemHandler.setStackInSlot(7+ i*2, ItemStack.EMPTY);
                                if (i == 0) itemHandler.setStackInSlot(stage.get(i), new ItemStack(itemHandler.getStackInSlot(stage.get(i)).getItem(), itemHandler.getStackInSlot(stage.get(i)).getCount() - 1));
                                blazeCount--;
                                return;
                            }

                        }
                        itemHandler.setStackInSlot(7+ i*2, result);
                        if (i == 0) itemHandler.setStackInSlot(stage.get(i), new ItemStack(itemHandler.getStackInSlot(stage.get(i)).getItem(), itemHandler.getStackInSlot(stage.get(i)).getCount() - 1));

                        stage.set(i, stage.get(i) + 1);
                        updateTimer.set(i, 0);
                        UPDATE_INTERVAL.set(i, ((stage.get(i) - 2) + 7) * 20);
                        blazeCount--;
                    }
                } else {
                    updateTimer.set(i, updateTimer.get(i) + 1);
                }
            }
        }
        sendBrewingUpdateToClient();
    }

    private void sendBrewingUpdateToClient() {
        if (level != null && !level.isClientSide) {
            level.players().forEach(player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    ModNetworking.sendToClient(new BrewingVarSendToClientPacket(blazeCount, waterCount), serverPlayer);
                }
            });
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("brewingInventory"));
        this.blazeCount = tag.getInt("BlazeCount");
        this.waterCount = tag.getInt("WaterCount");
        int size = tag.getInt("StageSize");
        this.stage.clear();
        for (int i = 0; i < size; i++) {
            this.stage.add(tag.getInt("Stage"+i));
        }
        size = tag.getInt("UpdateTimerSize");
        this.updateTimer.clear();
        for (int i = 0; i < size; i++) {
            this.updateTimer.add(tag.getInt("UpdateTimer"+i));
        }
        size = tag.getInt("UpdateIntervalSize");
        this.UPDATE_INTERVAL.clear();
        for (int i = 0; i < size; i++) {
            this.UPDATE_INTERVAL.add(tag.getInt("UpdateInterval"+i));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("brewingInventory", itemHandler.serializeNBT());
        tag.putInt("BlazeCount", this.blazeCount);
        tag.putInt("WaterCount", this.waterCount);
        int size = this.stage.size();
        tag.putInt("StageSize", size);
        for (int i = 0; i < size; i++) {
            tag.putInt("Stage"+i, this.stage.get(i));
        }
        size = this.updateTimer.size();
        tag.putInt("UpdateTimerSize", size);
        for (int i = 0; i < size; i++) {
            tag.putInt("UpdateTimer"+i, this.updateTimer.get(i));
        }
        size = this.UPDATE_INTERVAL.size();
        tag.putInt("UpdateIntervalSize", size);
        for (int i = 0; i < size; i++) {
            tag.putInt("UpdateInterval"+i, this.UPDATE_INTERVAL.get(i));
        }
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    private void notifyUpdate() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
