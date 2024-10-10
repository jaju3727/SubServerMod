package net.jaju.subservermod.screen.shopsystem;

import net.jaju.subservermod.ModContainers;
import net.jaju.subservermod.entity.ShopEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ShopContainer extends AbstractContainerMenu {
    private final ShopEntity shopEntity;

    public ShopContainer(int id, Inventory playerInventory, ShopEntity shopEntity) {
        super(ModContainers.SHOP_CONTAINER.get(), id);
        this.shopEntity = shopEntity;


        // 플레이어 인벤토리 슬롯 추가 (오른쪽)
        int startX = 120;
        int startY = -2;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, startX + j * 18, startY + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, startX + k * 18, startY + 58));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.shopEntity.isAlive() && player.distanceToSqr(this.shopEntity) < 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 27) {
                if (!this.moveItemStackTo(itemstack1, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public ShopEntity getShopEntity() {
        return shopEntity;
    }
}

