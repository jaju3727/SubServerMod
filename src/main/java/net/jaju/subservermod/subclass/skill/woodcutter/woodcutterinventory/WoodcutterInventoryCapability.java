package net.jaju.subservermod.subclass.skill.woodcutter.woodcutterinventory;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.jaju.subservermod.Subservermod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WoodcutterInventoryCapability implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<WoodcutterInventoryCapability> WOODCUTTER_INVENTORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private final ItemStackHandler inventory = new ItemStackHandler(9);
    private final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> inventory);

    public WoodcutterInventoryCapability() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == WOODCUTTER_INVENTORY_CAPABILITY ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return inventory.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inventory.deserializeNBT(nbt);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }
}