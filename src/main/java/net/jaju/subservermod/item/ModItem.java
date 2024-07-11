package net.jaju.subservermod.item;

import net.jaju.subservermod.Subservermod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Subservermod.MOD_ID);

    public static final RegistryObject<Item> CONSTRUNTING_ALLOW = ITEMS.register("constructingallow",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WHIPPED_CREAM = ITEMS.register("whipped_cream",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BUTTER = ITEMS.register("butter",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CREAM_BREAD = ITEMS.register("cream_bread",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CROISSANT = ITEMS.register("croissant",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRAFTING_TOOL = ITEMS.register("crafting_tool",
            () -> new DurableCustomItem(new Item.Properties().stacksTo(1), 16, 1));
    public static final RegistryObject<Item> REDSTONE_MARBLE = ITEMS.register("redstone_marble",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_CUBIC = ITEMS.register("diamond_cubic",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EMERALD_CUBIC = ITEMS.register("emerald_cubic",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GOLD_RING = ITEMS.register("gold_ring",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LAPIS_LAZULI_MARBLE = ITEMS.register("lapis_lazuli_marble",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
