package net.jaju.subservermod;

import net.jaju.subservermod.screen.shopsystem.ShopContainer;
import net.jaju.subservermod.screen.shopsystem.ShopScreen;
import net.jaju.subservermod.screen.subclass.alchemist.brewing.BrewingContainer;
import net.jaju.subservermod.screen.subclass.alchemist.brewing.BrewingScreen;
import net.jaju.subservermod.screen.subclass.farmer.large_oven.LargeOvenContainer;
import net.jaju.subservermod.screen.subclass.farmer.large_oven.LargeOvenScreen;
import net.jaju.subservermod.screen.subclass.farmer.middle_oven.MiddleOvenContainer;
import net.jaju.subservermod.screen.subclass.farmer.middle_oven.MiddleOvenScreen;
import net.jaju.subservermod.screen.subclass.miner.crafting.CraftingContainer;
import net.jaju.subservermod.screen.subclass.miner.crafting.CraftingScreen;
import net.jaju.subservermod.screen.subclass.farmer.oven.OvenContainer;
import net.jaju.subservermod.screen.subclass.farmer.oven.OvenScreen;
import net.jaju.subservermod.screen.subclass.miner.upgrade_crafting.UpgradeCraftingContainer;
import net.jaju.subservermod.screen.subclass.miner.upgrade_crafting.UpgradeCraftingScreen;
import net.jaju.subservermod.screen.subclass.woodcutter.woodcuttingunion.WoodcuttingUnionContainer;
import net.jaju.subservermod.screen.subclass.woodcutter.woodcuttingunion.WoodcuttingUnionScreen;
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
        MenuScreens.<OvenContainer, OvenScreen>register(ModContainers.OVEN_MENU.get(), OvenScreen::new);
        MenuScreens.<MiddleOvenContainer, MiddleOvenScreen>register(ModContainers.MIDDLE_OVEN_MENU.get(), MiddleOvenScreen::new);
        MenuScreens.<LargeOvenContainer, LargeOvenScreen>register(ModContainers.LARGE_OVEN_MENU.get(), LargeOvenScreen::new);
        MenuScreens.<CraftingContainer, CraftingScreen>register(ModContainers.CRAFTING_MENU.get(), CraftingScreen::new);
        MenuScreens.<UpgradeCraftingContainer, UpgradeCraftingScreen>register(ModContainers.UPGRADE_CRAFTING_MENU.get(), UpgradeCraftingScreen::new);
        MenuScreens.<BrewingContainer, BrewingScreen>register(ModContainers.BREWING_MENU.get(), BrewingScreen::new);
        MenuScreens.<WoodcuttingUnionContainer, WoodcuttingUnionScreen>register(ModContainers.WOODCUTTINGUNION_MENU.get(), WoodcuttingUnionScreen::new);
    }
}