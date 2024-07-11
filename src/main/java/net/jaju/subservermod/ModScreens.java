package net.jaju.subservermod;

import net.jaju.subservermod.shopsystem.screen.ShopContainer;
import net.jaju.subservermod.shopsystem.screen.ShopScreen;
import net.jaju.subservermod.subclass.skill.alchemist.brewing.BrewingContainer;
import net.jaju.subservermod.subclass.skill.alchemist.brewing.BrewingScreen;
import net.jaju.subservermod.subclass.skill.miner.crafting.CraftingContainer;
import net.jaju.subservermod.subclass.skill.miner.crafting.CraftingScreen;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenContainer;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenScreen;
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
        MenuScreens.<OvenContainer, OvenScreen>register(ModContainers.OVEN_MENU.get(), (screenContainer, inv, title) -> {
            return new OvenScreen(screenContainer, inv, title);
        });
        MenuScreens.<CraftingContainer, CraftingScreen>register(ModContainers.CRAFTING_MENU.get(), (screenContainer, inv, title) -> {
            return new CraftingScreen(screenContainer, inv, title);
        });
        MenuScreens.<BrewingContainer, BrewingScreen>register(ModContainers.BREWING_MENU.get(), (screenContainer, inv, title) -> {
            return new BrewingScreen(screenContainer, inv, title);
        });
    }
}