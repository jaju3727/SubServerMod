package net.jaju.subservermod.encyclopedia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.landsystem.LandManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class EncyclopediaManager {
    private static EncyclopediaManager INSTANCE;
    private static final HashMap<UUID, HashMap<String, Boolean>> categoryDiscoveries = new HashMap<>();
    private static final LinkedHashMap<String, Integer> encyclopedia = new LinkedHashMap<>();
    private static final HashMap<UUID, LinkedHashMap<Integer, Boolean>> giftGet = new HashMap<>();
    private static final LinkedHashMap<Integer, List<ItemStack>> giftList = new LinkedHashMap<>();
    private static final String FILE_PATH = "config/encyclopedia.json";
    private static final String FILE_PATH_2 = "config/gift.json";
    private static Boolean flag = false;
    private final Gson gson = new Gson();

    public EncyclopediaManager() {

        loadEncyclopedia();
    }

    public static EncyclopediaManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EncyclopediaManager();
        }
        return INSTANCE;
    }

    private static void addGifts() {
        addGift(10, List.of(new ItemStack((ModItem.BUTTER.get()), 2), new ItemStack((ModItem.BUTTER.get()), 2), new ItemStack((ModItem.BUTTER.get()), 2)));
        addGift(30, List.of(new ItemStack((ModItem.BUTTER.get()), 2)));
        addGift(40, List.of(new ItemStack((ModItem.BUTTER.get()), 2)));
        addGift(165, List.of(new ItemStack((ModItem.BUTTER.get()), 2)));
    }

    private static void addItems() {
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ACACIA_FENCE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ACACIA_BOAT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ACACIA_BUTTON)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ACACIA_PLANKS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ACACIA_SLAB)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.APPLE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BAKED_POTATO)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BEEF)).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BEETROOT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BEETROOT_SOUP).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BIRCH_BOAT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BIRCH_BUTTON).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BIRCH_DOOR).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BIRCH_FENCE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BIRCH_LOG).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BIRCH_PLANKS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BIRCH_SLAB).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BLAZE_POWDER).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BLAZE_ROD).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BONE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BONE_MEAL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BOOK).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BOW).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BOWL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BREAD).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BRICK).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BROWN_MUSHROOM).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.BUCKET).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CAKE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CARROT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CARROT_ON_A_STICK).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CAULDRON).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CHAIN).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CHARCOAL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CHEST).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CHICKEN).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CLOCK).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.COAL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.COMPASS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.COOKED_BEEF).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.COOKED_CHICKEN).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.COOKED_MUTTON).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.COOKED_PORKCHOP).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.COOKED_RABBIT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.COOKIE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.COPPER_INGOT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.CRAFTING_TABLE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_AXE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_BOOTS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_CHESTPLATE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_HELMET).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_HOE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_HORSE_ARMOR).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_LEGGINGS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_PICKAXE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_SHOVEL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_SWORD).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.DRIED_KELP).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.EGG).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.EMERALD).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.ENCHANTED_BOOK).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.ENDER_PEARL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.FEATHER).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.FERMENTED_SPIDER_EYE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.FIRE_CHARGE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.FISHING_ROD).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.FLINT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.FLINT_AND_STEEL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.FLOWER_POT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.FURNACE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GHAST_TEAR).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GLASS_BOTTLE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GLOWSTONE_DUST).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_APPLE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_AXE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_BOOTS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_CARROT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_CHESTPLATE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_HELMET).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_HOE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_HORSE_ARMOR).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_LEGGINGS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_PICKAXE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_SHOVEL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_SWORD).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLD_INGOT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GOLD_NUGGET).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.GUNPOWDER).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.HONEY_BOTTLE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_AXE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_BARS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_BOOTS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_CHESTPLATE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_DOOR).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_HELMET).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_HOE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_HORSE_ARMOR).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_INGOT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_LEGGINGS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_PICKAXE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_SHOVEL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.IRON_SWORD).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.ITEM_FRAME).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_BOAT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_BUTTON).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_DOOR).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_FENCE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_LOG).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_PLANKS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_SLAB).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LADDER).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LAPIS_LAZULI).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LAVA_BUCKET).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LEAD).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LEATHER).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LEATHER_BOOTS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LEATHER_CHESTPLATE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LEATHER_HELMET).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LEATHER_LEGGINGS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LIGHT_BLUE_DYE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LIGHT_GRAY_DYE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.LIME_DYE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.MAGMA_CREAM).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.MAP).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.MELON_SLICE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.MILK_BUCKET).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.MUSHROOM_STEW).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.MUTTON).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.NAME_TAG).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.NETHER_BRICK).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.NETHER_STAR).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.NETHER_WART).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.OAK_BOAT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.OAK_BUTTON).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.OAK_DOOR).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.OAK_FENCE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.OAK_LOG).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.OAK_PLANKS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.OAK_SLAB).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.ORANGE_DYE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.PAPER).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.PINK_DYE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.POISONOUS_POTATO).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.POTATO).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.POTION).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.PRISMARINE_CRYSTALS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.PUFFERFISH).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.PUMPKIN_PIE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.RABBIT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.RABBIT_FOOT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.RABBIT_HIDE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.RABBIT_STEW).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.RED_DYE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.ROTTEN_FLESH).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SADDLE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SALMON).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SHEARS).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SKELETON_SKULL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SLIME_BALL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SNOWBALL).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SPECTRAL_ARROW).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SPIDER_EYE).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SPRUCE_BOAT).toString(), 10);
        addItem(ForgeRegistries.ITEMS.getKey(Items.SPRUCE_BUTTON).toString(), 10);
    }

    private static void addItem(String itemLocation, int itemCount) {
        if (!encyclopedia.containsKey(itemLocation)) {
            encyclopedia.put(itemLocation, itemCount);
        }
    }

    private static void addGift(Integer giftNum, List<ItemStack> itemStacks) {
        if (!giftList.containsKey(giftNum)) giftList.put(giftNum, itemStacks);
    }

    public void initializePlayerDiscoveries(UUID playerUUID) {
        if (!flag) {
            addItems();
            addGifts();
            flag = true;
        }
        if (!categoryDiscoveries.containsKey(playerUUID)) {
            HashMap<String, Boolean> discoveries = new HashMap<>();
            for (String itemLocation : encyclopedia.keySet()) {
                discoveries.put(itemLocation, false);
            }
            categoryDiscoveries.put(playerUUID, discoveries);
            saveEncyclopedia();
        }
        if (!giftGet.containsKey(playerUUID)) {
            LinkedHashMap<Integer, Boolean> get = new LinkedHashMap<>();
            for (Integer num : giftList.keySet()) {
                get.put(num, false);
            }
            giftGet.put(playerUUID, get);
            saveEncyclopedia();
        }
    }

    public void discoverItem(UUID playerUUID, Item item) {
        String itemLocation = ForgeRegistries.ITEMS.getKey(item).toString();
        if (encyclopedia.containsKey(itemLocation)) {
            HashMap<String, Boolean> discoveries = categoryDiscoveries.get(playerUUID);
            if (discoveries != null) {
                discoveries.put(itemLocation, true);
                saveEncyclopedia();
            }
        }
    }

    public void getGift(UUID playerUUID, Integer num) {
        if (giftList.containsKey(num)) {
            HashMap<Integer, Boolean> discoveries = giftGet.get(playerUUID);
            if (discoveries != null) {
                discoveries.put(num, true);
                saveEncyclopedia();
            }
        }
    }

    public static LinkedHashMap<String, Integer> getEncyclopedia() {
        return encyclopedia;
    }

    public static LinkedHashMap<Integer, List<ItemStack>> getGiftList() {
        return giftList;
    }

    public static HashMap<String, Boolean> getDiscoveries(UUID playerUUID) {
        return categoryDiscoveries.getOrDefault(playerUUID, new HashMap<>());
    }

    public static LinkedHashMap<Integer, Boolean> getGiftGet(UUID playerUUID) {
        return giftGet.getOrDefault(playerUUID, new LinkedHashMap<>());
    }

    private void saveEncyclopedia() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(categoryDiscoveries, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(FILE_PATH_2)) {
            gson.toJson(giftGet, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEncyclopedia() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type discoveriesType = new TypeToken<HashMap<UUID, HashMap<String, Boolean>>>() {}.getType();
            HashMap<UUID, HashMap<String, Boolean>> loadedDiscoveries = gson.fromJson(reader, discoveriesType);
            if (loadedDiscoveries != null) {
                categoryDiscoveries.putAll(loadedDiscoveries);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileReader reader = new FileReader(FILE_PATH_2)) {
            Type discoveriesType = new TypeToken<HashMap<UUID, LinkedHashMap<Integer, Boolean>>>() {}.getType();
            HashMap<UUID, LinkedHashMap<Integer, Boolean>> loadedGiftGet = gson.fromJson(reader, discoveriesType);
            if (loadedGiftGet != null) {
                giftGet.putAll(loadedGiftGet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
