package net.jaju.subservermod;

import net.jaju.subservermod.shopsystem.entity.ShopEntity;
import net.jaju.subservermod.shopsystem.screen.ShopContainer;
import net.jaju.subservermod.subclass.skill.alchemist.brewing.BrewingContainer;
import net.jaju.subservermod.subclass.skill.farmer.middle_oven.MiddleOvenContainer;
import net.jaju.subservermod.subclass.skill.miner.crafting.CraftingContainer;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenContainer;
import net.jaju.subservermod.subclass.skill.woodcutter.woodcuttingunion.WoodcuttingUnionContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Subservermod.MOD_ID);

    public static final RegistryObject<MenuType<ShopContainer>> SHOP_CONTAINER = CONTAINERS.register("shop_container", () ->
            IForgeMenuType.create((windowId, inv, data) -> {
                int entityId = data.readInt();
                Level world = inv.player.level();
                ShopEntity entity = (ShopEntity) world.getEntity(entityId);
                return new ShopContainer(windowId, inv, entity);
            }));
    public static final RegistryObject<MenuType<OvenContainer>> OVEN_MENU = CONTAINERS.register("oven_menu", () ->
            IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new OvenContainer(windowId, inv, (inv.player.level().getBlockEntity(pos)).getBlockPos());
            }));
    public static final RegistryObject<MenuType<MiddleOvenContainer>> MIDDLE_OVEN_MENU = CONTAINERS.register("middle_oven_menu", () ->
            IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new MiddleOvenContainer(windowId, inv, (inv.player.level().getBlockEntity(pos)).getBlockPos());
            }));
    public static final RegistryObject<MenuType<CraftingContainer>> CRAFTING_MENU = CONTAINERS.register("crafting_menu", () ->
            IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new CraftingContainer(windowId, inv, (inv.player.level().getBlockEntity(pos)).getBlockPos());
            }));
    public static final RegistryObject<MenuType<BrewingContainer>> BREWING_MENU = CONTAINERS.register("brewing_menu", () ->
            IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new BrewingContainer(windowId, inv, (inv.player.level().getBlockEntity(pos)).getBlockPos());
            }));
    public static final RegistryObject<MenuType<WoodcuttingUnionContainer>> WOODCUTTINGUNION_MENU = CONTAINERS.register("woodcutting_union_menu", () ->
            IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new WoodcuttingUnionContainer(windowId, inv, (inv.player.level().getBlockEntity(pos)).getBlockPos());
            }));

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}