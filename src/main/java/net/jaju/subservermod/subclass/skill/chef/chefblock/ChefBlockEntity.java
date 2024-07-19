package net.jaju.subservermod.subclass.skill.chef.chefblock;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.item.ModItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChefBlockEntity extends BlockEntity {
    private List<ItemStack> itemStacks = new ArrayList<>();
    private int cookingOilCount = 0;
    private int butterCount = 0;
    private long tick = 20;
    private long cookTick = 20;
    private long overCookTick = 20;
    private int cookGauge = 0;
    private int overCookGauge = 0;
    private boolean cookFlag = false;
    private boolean overCookFlag = false;
    private boolean overCooked = false;

    public ChefBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHEF_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean getoverCooked() {
        return overCooked;
    }

    public void setoverCooked(boolean overCooked) {
        this.overCooked = overCooked;
        setChanged();
        notifyUpdate();
    }

    public boolean getcookFlag() {
        return cookFlag;
    }

    public void setcookFlag(boolean cookFlag) {
        this.cookFlag = cookFlag;
        setChanged();
        notifyUpdate();
    }

    public boolean getoverCookFlag() {
        return overCookFlag;
    }

    public void setoverCookFlag(boolean overCookFlag) {
        this.overCookFlag = overCookFlag;
        setChanged();
        notifyUpdate();
    }

    public long getTick() {
        return tick;
    }

    public void setTick(long tick) {
        this.tick = tick;
        setChanged();
        notifyUpdate();
    }

    public long getOverCookTick() {
        return overCookTick;
    }

    public void setOverCookTick(long overCookTick) {
        this.overCookTick = overCookTick;
        setChanged();
        notifyUpdate();
    }

    public long getCookTick() {
        return cookTick;
    }

    public void setCookTick(long cookTick) {
        this.cookTick = cookTick;
        setChanged();
        notifyUpdate();
    }

    public int getcookGauge() {
        return cookGauge;
    }

    public void setcookGauge(int cookGauge) {
        this.cookGauge = cookGauge;
        setChanged();
        notifyUpdate();
    }

    public int getoverCookGauge() {
        return overCookGauge;
    }

    public void setoverCookGauge(int overCookGauge) {
        this.overCookGauge = overCookGauge;
        setChanged();
        notifyUpdate();
    }

    public int getCookingOilCount() {
        return cookingOilCount;
    }

    public int getButterCount() {
        return butterCount;
    }

    public void setCookingOilCount(int cookingOilCount) {
        this.cookingOilCount = cookingOilCount;
        setChanged();
        notifyUpdate();
    }

    public void setButterCount(int butterCount) {
        this.butterCount = butterCount;
        setChanged();
        notifyUpdate();
    }

    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }

    public void setItemStacks(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
        setChanged();
        notifyUpdate();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ChefBlockEntity entity) {
        if (!level.isClientSide) {
            if (entity.getcookFlag()){
                long cookTick = System.currentTimeMillis() - entity.getCookTick();
                if (cookTick > entity.getcookGauge() * 1000L) {
                    entity.setcookFlag(false);
                    entity.setoverCookFlag(true);
                    entity.setoverCookGauge(4);
                    entity.setOverCookTick(System.currentTimeMillis());

                    List<ItemStack> itemStacks = entity.getItemStacks();
                    ItemStack itemStack = itemStacks.get(1);
                    if (itemStack.getItem() == Items.BEEF) {
                        itemStacks.remove(itemStack);
                        itemStacks.add(new ItemStack(ModItem.BEEF_STEAK.get()));
                        int nextCount = entity.getButterCount() - 1;
                        if (nextCount == 0) itemStacks.remove(0);
                        entity.setButterCount(nextCount);
                    } else if (itemStack.getItem() == Items.PORKCHOP) {
                        itemStacks.remove(itemStack);
                        itemStacks.add(new ItemStack(ModItem.PORK_BELLY.get()));
                        int nextCount = entity.getCookingOilCount() - 1;
                        if (nextCount == 0) itemStacks.remove(0);
                        entity.setCookingOilCount(nextCount);
                    } else if (itemStack.getItem() == Items.CHICKEN) {
                        itemStacks.remove(itemStack);
                        itemStacks.add(new ItemStack(ModItem.ROAST_CHICKEN.get()));
                        int nextCount = entity.getCookingOilCount() - 1;
                        if (nextCount == 0) itemStacks.remove(0);
                        entity.setCookingOilCount(nextCount);
                    } else if (itemStack.getItem() == Items.MUTTON) {
                        itemStacks.remove(itemStack);
                        itemStacks.add(new ItemStack(ModItem.LAMB_STEAK.get()));
                        int nextCount = entity.getButterCount() - 1;
                        if (nextCount == 0) itemStacks.remove(0);
                        entity.setButterCount(nextCount);
                    } else if (itemStack.getItem() == Items.EGG) {
                        itemStacks.remove(itemStack);
                        itemStacks.add(new ItemStack(ModItem.FRIED_EGG.get()));
                        int nextCount = entity.getCookingOilCount() - 1;
                        if (nextCount == 0) itemStacks.remove(0);
                        entity.setCookingOilCount(nextCount);
                    }
                    entity.setItemStacks(itemStacks);
                }
            }
            if (entity.getoverCookFlag()) {
                long overCookTick = System.currentTimeMillis() - entity.getOverCookTick();
                if (overCookTick > entity.getoverCookGauge() * 1000L) {
                    List<ItemStack> itemStacks = new ArrayList<>(entity.getItemStacks());
                    for (Iterator<ItemStack> iterator = itemStacks.iterator(); iterator.hasNext();) {
                        ItemStack itemStack = iterator.next();
                        if (itemStack.getItem() == ModItem.BEEF_STEAK.get() || itemStack.getItem() == ModItem.PORK_BELLY.get() || itemStack.getItem() == ModItem.ROAST_CHICKEN.get() || itemStack.getItem() == ModItem.LAMB_STEAK.get() || itemStack.getItem() == ModItem.FRIED_EGG.get()) {
                            iterator.remove();
                            entity.setoverCookFlag(false);
                            entity.setoverCooked(true);
                        }
                    }
                    entity.setItemStacks(itemStacks);
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemStacks.clear();
        int size = tag.getInt("size");
        for (int i = 0; i < size; i++) {
            itemStacks.add(ItemStack.of(tag.getCompound("item" + i)));
        }
        this.cookingOilCount = tag.getInt("cookingOilCount");
        this.butterCount = tag.getInt("butterCount");
        this.tick = tag.getLong("tick");
        this.cookTick = tag.getLong("cookTick");
        this.overCookTick = tag.getLong("overCookTick");
        this.cookGauge = tag.getInt("cookGauge");
        this.overCookGauge = tag.getInt("overCookGauge");
        this.cookFlag = tag.getBoolean("cookFlag");
        this.overCookFlag = tag.getBoolean("overCookFlag");
        this.overCooked = tag.getBoolean("overCooked");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("size", itemStacks.size());
        for (int i = 0; i < itemStacks.size(); i++) {
            CompoundTag itemTag = new CompoundTag();
            itemStacks.get(i).save(itemTag);
            tag.put("item" + i, itemTag);
        }
        tag.putInt("cookingOilCount", this.cookingOilCount);
        tag.putInt("butterCount", this.butterCount);
        tag.putLong("tick", this.tick);
        tag.putLong("cookTick", this.cookTick);
        tag.putLong("overCookTick", this.overCookTick);
        tag.putInt("cookGauge", this.cookGauge);
        tag.putInt("overCookGauge", this.overCookGauge);
        tag.putBoolean("cookFlag", this.cookFlag);
        tag.putBoolean("overCookFlag", this.overCookFlag);
        tag.putBoolean("overCooked", this.overCooked);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    private void notifyUpdate() {
        if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
