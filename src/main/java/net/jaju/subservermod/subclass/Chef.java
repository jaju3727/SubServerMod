package net.jaju.subservermod.subclass;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.skill.chef.ChefSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Collections;

public class Chef extends BaseClass {
    private transient ChefSkill chefSkill;

    public Chef(int level, String playerName) {
        super("Chef", level, playerName);
        chefSkill = new ChefSkill(this);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        if(getLevel() >= 2) {
            RecipeManager recipeManager = player.server.getRecipeManager();

            recipeManager.byKey(new ResourceLocation(Subservermod.MOD_ID, "butter_recipe")).ifPresent(recipe -> {
                player.awardRecipes(Collections.singleton(recipe));
            });
            recipeManager.byKey(new ResourceLocation(Subservermod.MOD_ID, "cooking_oil_recipe")).ifPresent(recipe -> {
                player.awardRecipes(Collections.singleton(recipe));
            });
        }
    }
}
