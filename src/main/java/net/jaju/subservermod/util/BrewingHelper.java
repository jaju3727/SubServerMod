package net.jaju.subservermod.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.HashMap;
import java.util.Map;

public class BrewingHelper {

    private static final Map<Potion, Map<Item, Potion>> potionMixes = new HashMap<>();

    static {
        initializeVanillaBrewingRecipes();
    }

    private static void initializeVanillaBrewingRecipes() {
        // 어색한 포션 생성
        addPotionMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        // 신속의 포션
        addPotionMix(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
        // 점프 강화의 포션
        addPotionMix(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
        // 힘의 포션
        addPotionMix(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        // 독의 포션
        addPotionMix(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
        // 재생의 포션
        addPotionMix(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        // 화염 저항의 포션
        addPotionMix(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        // 야간 투시의 포션
        addPotionMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        // 수중 호흡의 포션
        addPotionMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        // 거북이의 힘 포션
        addPotionMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        // 느린 낙하의 포션
        addPotionMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        // 투명의 포션
        addPotionMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        // 나약함의 포션
        addPotionMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        // 나약함의 포션 +
        addPotionMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
        
    }

    // 포션 믹스를 추가하는 메서드
    private static void addPotionMix(Potion basePotion, Item ingredient, Potion resultPotion) {
        potionMixes.computeIfAbsent(basePotion, k -> new HashMap<>()).put(ingredient, resultPotion);
    }

    // 주어진 재료와 기본 물약을 사용하여 결과 물약을 찾는 메서드
    public static ItemStack getPotionResult(ItemStack base, ItemStack ingredient) {
        if (base.getItem() == Items.POTION) {
            Potion basePotion = PotionUtils.getPotion(base);
            Item ingredientItem = ingredient.getItem();

            Map<Item, Potion> mixes = potionMixes.get(basePotion);
            if (mixes != null && mixes.containsKey(ingredientItem)) {
                return PotionUtils.setPotion(new ItemStack(Items.POTION), mixes.get(ingredientItem));
            }
        }
        return ItemStack.EMPTY;
    }

}
