package net.jaju.subservermod.encyclopedia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.landsystem.LandManager;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

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

    public Map<UUID, Integer> getTopDiscoverers(int num) {
        Map<UUID, Integer> playerDiscoveriesCount = new HashMap<>();
        for (Map.Entry<UUID, HashMap<String, Boolean>> entry : categoryDiscoveries.entrySet()) {
            int count = (int) entry.getValue().values().stream().filter(Boolean::booleanValue).count();
            playerDiscoveriesCount.put(entry.getKey(), count);
        }
        return playerDiscoveriesCount.entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(num)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }


    private static ItemStack returnSub(String num) {
        ItemStack subCoin = new ItemStack(ModItem.SUB_COIN.get(), 1);
        ListTag subList = new ListTag();
        subList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(num+"원"))));
        subCoin.getOrCreateTagElement("display").put("Lore", subList);

        return subCoin;
    }

    private static void addGifts() {

        addGift(10, List.of(returnSub("10")));
        addGift(20, List.of(returnSub("30"), new ItemStack((ModItem.HOLY_GRAIL.get()), 1)));
        addGift(30, List.of(returnSub("30"), new ItemStack((ModItem.CONSTRUNTING_ALLOW.get()), 1)));
        addGift(50, List.of(returnSub("60"),
                new ItemStack((ModItem.CLASS_FORMER_1.get()), 6)));
        ItemStack randomBoxItem = new ItemStack(ModItem.RANDOMBOX.get(), 1);

        MutableComponent lore = Component.literal("clothes");
        ListTag loreList = new ListTag();
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(lore)));
        randomBoxItem.getOrCreateTagElement("display").put("Lore", loreList);
        addGift(100, List.of(returnSub("120"), randomBoxItem));
        addGift(150, List.of(returnSub("180"),
                new ItemStack((ModItem.CLASS_FORMER_2.get()), 6)));
        addGift(200, List.of(returnSub("300"),
                new ItemStack((ModItem.SPECIAL_STORE_TELEPORT.get()), 1)));
        addGift(250, List.of(returnSub("600"),
                new ItemStack((ModItem.CLASS_FORMER_3.get()), 1)));
        addGift(300, List.of(returnSub("1200")));
    }

    private static void addItems() {
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GRASS_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DIRT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ROOTED_DIRT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PODZOL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MYCELIUM)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUD)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PACKED_MUD)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MOSS_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.STONE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DEEPSLATE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DIORITE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ANDESITE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GRANITE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CALCITE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TUFF)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.COAL_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.COPPER_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.IRON_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GOLD_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.REDSTONE_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.LAPIS_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.EMERALD_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DEEPSLATE_COPPER_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DEEPSLATE_IRON_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DEEPSLATE_GOLD_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DEEPSLATE_REDSTONE_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DEEPSLATE_LAPIS_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DEEPSLATE_DIAMOND_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.OAK_LOG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SPRUCE_LOG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BIRCH_LOG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_LOG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ACACIA_LOG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DARK_OAK_LOG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CHERRY_LOG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MANGROVE_LOG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.OAK_SAPLING)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SPRUCE_SAPLING)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BIRCH_SAPLING)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_SAPLING)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ACACIA_SAPLING)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DARK_OAK_SAPLING)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CHERRY_SAPLING)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MANGROVE_PROPAGULE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.OAK_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SPRUCE_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BIRCH_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.JUNGLE_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ACACIA_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DARK_OAK_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CHERRY_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MANGROVE_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.APPLE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PINK_PETALS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MANGROVE_ROOTS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.FLOWERING_AZALEA_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.AZALEA_LEAVES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DARK_PRISMARINE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PRISMARINE_BRICKS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PRISMARINE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SPONGE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SEA_LANTERN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PRISMARINE_CRYSTALS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PRISMARINE_SHARD)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SEA_PICKLE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DRIED_KELP_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.LILY_PAD)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TURTLE_EGG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GLOW_INK_SAC)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.INK_SAC)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TUBE_CORAL_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BRAIN_CORAL_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BUBBLE_CORAL_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.FIRE_CORAL_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HORN_CORAL_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TUBE_CORAL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BRAIN_CORAL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BUBBLE_CORAL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.FIRE_CORAL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HORN_CORAL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TUBE_CORAL_FAN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BRAIN_CORAL_FAN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BUBBLE_CORAL_FAN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.FIRE_CORAL_FAN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HORN_CORAL_FAN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SALMON_BUCKET)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TROPICAL_FISH_BUCKET)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PUFFERFISH_BUCKET)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.COD_BUCKET)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.AXOLOTL_BUCKET)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TADPOLE_BUCKET)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SCULK_SHRIEKER)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.COBWEB)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GLOW_LICHEN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SPORE_BLOSSOM)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BIG_DRIPLEAF)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SMALL_DRIPLEAF)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GLOW_BERRIES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HANGING_ROOTS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.POINTED_DRIPSTONE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SMALL_AMETHYST_BUD)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ECHO_SHARD)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BEEF)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PORKCHOP)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CHICKEN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.FEATHER)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUTTON)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.RABBIT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.RABBIT_HIDE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.RABBIT_FOOT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ROTTEN_FLESH)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BONE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GUNPOWDER)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SPIDER_EYE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SLIME_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PHANTOM_MEMBRANE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MILK_BUCKET)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WHEAT_SEEDS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BAMBOO)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PUMPKIN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MELON)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CARROT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.POTATO)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.POISONOUS_POTATO)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BEETROOT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SWEET_BERRIES)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SUGAR_CANE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CACTUS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.COCOA_BEANS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.COOKIE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BEE_NEST)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HONEYCOMB)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HONEY_BOTTLE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HONEY_BLOCK)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSHROOM_STEW)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.EGG)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SAND)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.RED_SAND)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GRAVEL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BELL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.VINE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WHITE_WOOL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BROWN_MUSHROOM_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSHROOM_STEM)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.RED_MUSHROOM_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ICE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SNOW_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.POWDER_SNOW_BUCKET)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CHARCOAL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.FLINT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.OXIDIZED_COPPER)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GLASS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CLAY)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GLASS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TERRACOTTA)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WHITE_CONCRETE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.OBSIDIAN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CRYING_OBSIDIAN)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.NETHERRACK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BLACKSTONE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BASALT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CRIMSON_NYLIUM)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WARPED_NYLIUM)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SOUL_SAND)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SOUL_SOIL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.NETHER_GOLD_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GILDED_BLACKSTONE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.NETHER_QUARTZ_ORE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MAGMA_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ANCIENT_DEBRIS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GLOWSTONE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CRIMSON_STEM)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WARPED_STEM)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.NETHER_WART)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WARPED_WART_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CRIMSON_FUNGUS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WARPED_FUNGUS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GHAST_TEAR)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BLAZE_ROD)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SHROOMLIGHT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SPECTRAL_ARROW)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.RED_NETHER_BRICKS)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ENDER_PEARL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.END_STONE)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DRAGON_BREATH)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CHORUS_FRUIT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CHORUS_PLANT)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.END_ROD)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PURPUR_BLOCK)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SHULKER_SHELL)).toString(), 10);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SNIFFER_EGG)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TORCHFLOWER)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PITCHER_PLANT)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MINER_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MOURNER_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PLENTY_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PRIZE_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SHEAF_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SHELTER_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SKULL_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HEART_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.FRIEND_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HOWL_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HEARTBREAK_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SNORT_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ANGLER_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ARCHER_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ARMS_UP_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BLADE_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BREWER_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.BURN_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DANGER_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.EXPLORER_POTTERY_SHERD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_RELIC)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_PIGSTEP)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_5)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_OTHERSIDE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_WAIT)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_11)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_WARD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_13)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_BLOCKS)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_CAT)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_STAL)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_MELLOHI)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_MALL)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_FAR)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_CHIRP)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.MUSIC_DISC_STRAD)).toString(), 1);
        addItem("minecraft:water_breathing_potion", 1);
        addItem("minecraft:slow_falling_potion", 1);
        addItem("minecraft:weakness_potion", 1);
        addItem("minecraft:strength_potion", 1);
        addItem("minecraft:poison_potion", 1);
        addItem("minecraft:harming_potion", 1);
        addItem("minecraft:night_vision_potion", 1);
        addItem("minecraft:invisibility_potion", 1);
        addItem("minecraft:leaping_potion", 1);
        addItem("minecraft:healing_potion", 1);
        addItem("minecraft:regeneration_potion", 1);
        addItem("minecraft:slowness_potion", 1);
        addItem("minecraft:swiftness_potion", 1);
        addItem("minecraft:fire_resistance_potion", 1);
        addItem("minecraft:turtle_master_potion", 1);
        addItem("minecraft:ponder_goat_horn", 1);
        addItem("minecraft:sing_goat_horn", 1);
        addItem("minecraft:seek_goat_horn", 1);
        addItem("minecraft:feel_goat_horn", 1);
        addItem("minecraft:admire_goat_horn", 1);
        addItem("minecraft:call_goat_horn", 1);
        addItem("minecraft:yearn_goat_horn", 1);
        addItem("minecraft:dream_goat_horn", 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ZOMBIE_HEAD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SKELETON_SKULL)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CREEPER_HEAD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.PIGLIN_HEAD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DRAGON_HEAD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DEEPSLATE_COAL_ORE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DEEPSLATE_EMERALD_ORE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SPYGLASS)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.NETHER_STAR)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WITHER_ROSE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ELYTRA)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SCULK_CATALYST)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ENCHANTED_GOLDEN_APPLE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.EXPERIENCE_BOTTLE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.NAUTILUS_SHELL)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.HEART_OF_THE_SEA)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TRIDENT)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.LEATHER_HORSE_ARMOR)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.IRON_HORSE_ARMOR)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GOLDEN_HORSE_ARMOR)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.DIAMOND_HORSE_ARMOR)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SADDLE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.NAME_TAG)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.WHITE_BANNER)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.TOTEM_OF_UNDYING)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.FLOWER_BANNER_PATTERN)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.LIGHTNING_ROD)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.SMOOTH_STONE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.ANVIL)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.GRINDSTONE)).toString(), 1);
        addItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.CAKE)).toString(), 1);
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

    public void discoverItem(UUID playerUUID, String itemLocation) {
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
