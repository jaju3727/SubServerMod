package net.jaju.subservermod.subclass.skill.fisherman.fishing_rod;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.item.ModItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.loot.LootTables;

import java.util.Random;

public class FishingRodBlockEntity extends BlockEntity {
    private ItemStack itemStack = new ItemStack(Items.AIR);
    private long fishingTick = 0;
    private boolean catchFish = false;
    private Random random = new Random();

    public FishingRodBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FISHING_ROD_BLOCK_ENTITY.get(), pos, state);
    }

    public void setCatchFish(boolean catchFish) {
        this.catchFish = catchFish;
        setChanged();
        notifyUpdate();
    }

    public boolean isCatchFish() {
        return catchFish;
    }
    public long getFishingTick() {
        return fishingTick;
    }

    public void setFishingTick(long fishingTick) {
        this.fishingTick = fishingTick + random.nextInt(5, 30) * 1000L;
        setChanged();
        notifyUpdate();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        setChanged();
        notifyUpdate();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FishingRodBlockEntity entity) {
        if (!level.isClientSide) {
            if (entity.getItemStack().getItem() != Items.AIR) {
                long tick = System.currentTimeMillis() - entity.getFishingTick();
                if (tick > 0) {
                    entity.setCatchFish(true);
                }
                if (tick > 5000) {
                    entity.setCatchFish(false);
                    entity.setFishingTick(System.currentTimeMillis());
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.itemStack = ItemStack.of(tag.getCompound("itemStack"));
        this.catchFish = tag.getBoolean("catchFish");
        this.fishingTick = tag.getLong("fishingTick");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        CompoundTag itemTag = new CompoundTag();
        this.itemStack.save(itemTag);
        tag.put("itemStack", itemTag);
        tag.putBoolean("catchFish", this.catchFish);
        tag.putLong("fishingTick", this.fishingTick);
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
