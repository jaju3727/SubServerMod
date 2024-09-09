package net.jaju.subservermod.item;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Subservermod.MOD_ID);

    public static final RegistryObject<Item> CONSTRUNTING_ALLOW = ITEMS.register("constructingallow",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HOLY_GRAIL = ITEMS.register("holy_grail",
            () -> new CustomItemWithCustomName(new Item.Properties(), "§5성배", "장비 아이템을 성배에 드랍 시 성배가 적용됩니다."));
    public static final RegistryObject<Item> INVENTORY_SAVE_ITEM = ITEMS.register("inventory_save_item",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RANDOMBOX = ITEMS.register("randombox",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WHIPPED_CREAM = ITEMS.register("whipped_cream",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BUTTER = ITEMS.register("butter",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CREAM_BREAD = ITEMS.register("cream_bread",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CROISSANT = ITEMS.register("croissant",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BAGUETTE = ITEMS.register("baguette",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHOCOLATE = ITEMS.register("chocolate",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BROWNIE = ITEMS.register("brownie",
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
    public static final RegistryObject<Item> WOODEN_CHAIR = ITEMS.register("wooden_chair",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOODEN_TABLE = ITEMS.register("wooden_table",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOODEN_CUTTING_BOARD = ITEMS.register("wooden_cutting_board",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOODEN_SHELF = ITEMS.register("wooden_shelf",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOODEN_DRAWER = ITEMS.register("wooden_drawer",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> APPLE_DIFFUSER = ITEMS.register("apple_diffuser",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COOKING_OIL = ITEMS.register("cooking_oil",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BEEF_STEAK = ITEMS.register("beef_steak",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LAMB_STEAK = ITEMS.register("lamb_steak",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ROAST_CHICKEN = ITEMS.register("roast_chicken",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORK_BELLY = ITEMS.register("pork_belly",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FRIED_EGG = ITEMS.register("fried_egg",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COD_RAW_FISH = ITEMS.register("cod_raw_fish",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SALMON_RAW_FISH = ITEMS.register("salmon_raw_fish",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SQUID = ITEMS.register("squid",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SQUID_SASHIMI = ITEMS.register("squid_sashimi",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SASHIMI_KNIFE = ITEMS.register("sashimi_knife",
            () -> new DurableCustomItem(new Item.Properties().stacksTo(1), 128, 1));
    public static final RegistryObject<Item> SUB_COIN = ITEMS.register("sub_coin",
            () -> new CoinItem(new Item.Properties(), "sub_coin"));
    public static final RegistryObject<Item> CHEF_COIN = ITEMS.register("chef_coin",
            () -> new CoinItem(new Item.Properties(), "chef_coin"));
    public static final RegistryObject<Item> FARMER_COIN = ITEMS.register("farmer_coin",
            () -> new CoinItem(new Item.Properties(), "farmer_coin"));
    public static final RegistryObject<Item> FISHERMAN_COIN = ITEMS.register("fisherman_coin",
            () -> new CoinItem(new Item.Properties(), "fisherman_coin"));
    public static final RegistryObject<Item> ALCHEMIST_COIN = ITEMS.register("alchemist_coin",
            () -> new CoinItem(new Item.Properties(), "alchemist_coin"));
    public static final RegistryObject<Item> MINER_COIN = ITEMS.register("miner_coin",
            () -> new CoinItem(new Item.Properties(), "miner_coin"));
    public static final RegistryObject<Item> WOODCUTTER_COIN = ITEMS.register("woodcutter_coin",
            () -> new CoinItem(new Item.Properties(), "woodcutter_coin"));
    public static final RegistryObject<Item> CLASS_FORMER_1 = ITEMS.register("class_former_1",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CLASS_FORMER_2 = ITEMS.register("class_former_2",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CLASS_FORMER_3 = ITEMS.register("class_former_3",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FARMER_FORMER_1 = ITEMS.register("farmer_former_1",
            () -> new FormerItem(new Item.Properties(), (short) 1, "Farmer"));
    public static final RegistryObject<Item> CHEF_FORMER_1 = ITEMS.register("chef_former_1",
            () -> new FormerItem(new Item.Properties(), (short) 1, "Chef"));
    public static final RegistryObject<Item> FISHERMAN_FORMER_1 = ITEMS.register("fisherman_former_1",
            () -> new FormerItem(new Item.Properties(), (short) 1, "Fisherman"));
    public static final RegistryObject<Item> ALCHEMIST_FORMER_1 = ITEMS.register("alchemist_former_1",
            () -> new FormerItem(new Item.Properties(), (short) 1, "Alchemist"));
    public static final RegistryObject<Item> MINER_FORMER_1 = ITEMS.register("miner_former_1",
            () -> new FormerItem(new Item.Properties(), (short) 1, "Miner"));
    public static final RegistryObject<Item> WOODCUTTER_FORMER_1 = ITEMS.register("woodcutter_former_1",
            () -> new FormerItem(new Item.Properties(), (short) 1, "Woodcutter"));
    public static final RegistryObject<Item> FARMER_FORMER_2 = ITEMS.register("farmer_former_2",
            () -> new FormerItem(new Item.Properties(), (short) 2, "Farmer"));
    public static final RegistryObject<Item> CHEF_FORMER_2= ITEMS.register("chef_former_2",
            () -> new FormerItem(new Item.Properties(), (short) 2, "Chef"));
    public static final RegistryObject<Item> FISHERMAN_FORMER_2 = ITEMS.register("fisherman_former_2",
            () -> new FormerItem(new Item.Properties(), (short) 2, "Fisherman"));
    public static final RegistryObject<Item> ALCHEMIST_FORMER_2 = ITEMS.register("alchemist_former_2",
            () -> new FormerItem(new Item.Properties(), (short) 2, "Alchemist"));
    public static final RegistryObject<Item> MINER_FORMER_2 = ITEMS.register("miner_former_2",
            () -> new FormerItem(new Item.Properties(), (short) 2, "Miner"));
    public static final RegistryObject<Item> WOODCUTTER_FORMER_2 = ITEMS.register("woodcutter_former_2",
            () -> new FormerItem(new Item.Properties(), (short) 2, "Woodcutter"));
    public static final RegistryObject<Item> FARMER_FORMER_3 = ITEMS.register("farmer_former_3",
            () -> new FormerItem(new Item.Properties(), (short) 3, "Farmer"));
    public static final RegistryObject<Item> CHEF_FORMER_3 = ITEMS.register("chef_former_3",
            () -> new FormerItem(new Item.Properties(), (short) 3, "Chef"));
    public static final RegistryObject<Item> FISHERMAN_FORMER_3 = ITEMS.register("fisherman_former_3",
            () -> new FormerItem(new Item.Properties(), (short) 3, "Fisherman"));
    public static final RegistryObject<Item> ALCHEMIST_FORMER_3 = ITEMS.register("alchemist_former_3",
            () -> new FormerItem(new Item.Properties(), (short) 3, "Alchemist"));
    public static final RegistryObject<Item> MINER_FORMER_3 = ITEMS.register("miner_former_3",
            () -> new FormerItem(new Item.Properties(), (short) 3, "Miner"));
    public static final RegistryObject<Item> WOODCUTTER_FORMER_3 = ITEMS.register("woodcutter_former_3",
            () -> new FormerItem(new Item.Properties(), (short) 3, "Woodcutter"));
    public static final RegistryObject<Item> LOW_CLASS_HEALING_POTION = ITEMS.register("low_class_healing_potion",
            () -> new HealItem(new Item.Properties(), 4, 20));
    public static final RegistryObject<Item> MIDDLE_CLASS_HEALING_POTION = ITEMS.register("middle_class_healing_potion",
            () -> new HealItem(new Item.Properties(), 10, 30));
    public static final RegistryObject<Item> HIGH_CLASS_HEALING_POTION = ITEMS.register("high_class_healing_potion",
            () -> new HealItem(new Item.Properties(), 20, 40));
    public static final RegistryObject<Item> TORY_BURGER = ITEMS.register("tory_burger",
            () -> new ToryBurgerItem(new Item.Properties().food(ModFoods.TORY_BURGER), "§6토리버거", "구리시 갈매순환로 204번길 119 파크위버 126호 토리버거"));
    public static final RegistryObject<Item> SPECIAL_STORE_TELEPORT = ITEMS.register("special_store_teleport",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GEMSHORE_TELEPORT = ITEMS.register("gemshore_teleport",
            () -> new TeleportItem(new Item.Properties(), Level.OVERWORLD, new BlockPos(3010, 68, -3999)));
    public static final RegistryObject<Item> PERRYGRASS_TELEPORT = ITEMS.register("perrygrass_teleport",
            () -> new TeleportItem(new Item.Properties(), Level.OVERWORLD, new BlockPos(-372, 68, -20)));
    public static final RegistryObject<Item> RUNEGROVE_TELEPORT = ITEMS.register("runegrove_teleport",
            () -> new TeleportItem(new Item.Properties(), Level.OVERWORLD, new BlockPos(-2750, 83, 2518)));
    public static final RegistryObject<Item> STAT_RESET = ITEMS.register("stat_reset",
            () -> new StatResetItem(new Item.Properties()));
    public static final RegistryObject<Item> STAT_POINT = ITEMS.register("stat_point",
            () -> new StatPointItem(new Item.Properties()));
    public static final RegistryObject<Item> CLASS_TRADE = ITEMS.register("class_trade",
            () -> new ClassResetItem(new Item.Properties()));
    public static final RegistryObject<Item> HOWSWEET = ITEMS.register("howsweet",
            () -> new RecordItem(6, ModSounds.HOWSWEET_SOUND,
                    new Item.Properties().stacksTo(1), 180));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
