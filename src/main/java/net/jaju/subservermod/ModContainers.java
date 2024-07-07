package net.jaju.subservermod;

import net.jaju.subservermod.shopsystem.entity.ShopEntity;
import net.jaju.subservermod.shopsystem.screen.ShopContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}