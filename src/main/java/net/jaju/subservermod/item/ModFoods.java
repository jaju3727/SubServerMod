package net.jaju.subservermod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties MIJUNG = new FoodProperties.Builder().nutrition(7).fast()
            .saturationMod(0.2f).build();
}
