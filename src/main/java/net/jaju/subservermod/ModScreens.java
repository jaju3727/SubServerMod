package net.jaju.subservermod;

import net.jaju.subservermod.shopsystem.screen.ShopContainer;
import net.jaju.subservermod.shopsystem.screen.ShopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModScreens {
    @OnlyIn(Dist.CLIENT)
    public static void register() {
        MenuScreens.<ShopContainer, ShopScreen>register(ModContainers.SHOP_CONTAINER.get(), (screenContainer, inv, title) -> {
            Player player = Minecraft.getInstance().player;
            return new ShopScreen(screenContainer, inv, title, player);
        });
    }
}