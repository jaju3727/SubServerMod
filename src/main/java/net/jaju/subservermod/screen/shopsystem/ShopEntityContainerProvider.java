package net.jaju.subservermod.screen.shopsystem;

import net.jaju.subservermod.entity.ShopEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;

public class ShopEntityContainerProvider implements MenuProvider {
    private final ShopEntity shopEntity;

    public ShopEntityContainerProvider(ShopEntity shopEntity) {
        this.shopEntity = shopEntity;
    }

    @Override
    public Component getDisplayName() {
        return shopEntity.getCustomName();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
        return new ShopContainer(id, playerInventory, shopEntity);
    }
}
