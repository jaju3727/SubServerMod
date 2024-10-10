package net.jaju.subservermod.blocks.subclass.fisherman.raw_fished;

import net.jaju.subservermod.blocks.ModBlockEntities;
import net.jaju.subservermod.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CuttingBoardBlockEntity extends BlockEntity {
    private Item item = Items.AIR;
    private int clickCount = 0;
    private long clickTime = 0;

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CUTTING_BOARD_BLOCK_ENTITY.get(), pos, state);
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
        setChanged();
        notifyUpdate();
    }

    public long getClickTime() {
        return clickTime;
    }

    public void setClickTime(long clickTime) {
        this.clickTime = clickTime;
        setChanged();
        notifyUpdate();
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        setChanged();
        notifyUpdate();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CuttingBoardBlockEntity entity) {
        if (!level.isClientSide) {
            if (entity.getClickCount() == 0) {
                Item item = entity.getItem();
                if (item == Items.COD) {
                    entity.setItem(ModItems.COD_RAW_FISH.get());
                }
                if (item == Items.SALMON) {
                    entity.setItem(ModItems.SALMON_RAW_FISH.get());
                }
                if (item == ModItems.SQUID.get()) {
                    entity.setItem(ModItems.SQUID_SASHIMI.get());
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.item = ItemStack.of(tag.getCompound("item")).getItem();
        this.clickCount = tag.getInt("clickCount");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        CompoundTag itemTag = new CompoundTag();
        new ItemStack(this.item).save(itemTag);
        tag.put("item", itemTag);
        tag.putInt("clickCount", this.clickCount);
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
