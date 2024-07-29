package net.jaju.subservermod.subclass;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.skill.farmer.FarmerSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Collections;

public class Farmer extends BaseClass {
    private transient FarmerSkill farmerSkill;

    public Farmer(int level, String playerName) {
        super("Farmer", level, playerName);
        farmerSkill = new FarmerSkill(this);

    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        if (getLevel() == 2) {
            RecipeManager recipeManager = player.server.getRecipeManager();

            recipeManager.byKey(new ResourceLocation(Subservermod.MOD_ID, "butter_recipe")).ifPresent(recipe -> {
                player.awardRecipes(Collections.singleton(recipe));
            });
            recipeManager.byKey(new ResourceLocation(Subservermod.MOD_ID, "whipped_cream_recipe")).ifPresent(recipe -> {
                player.awardRecipes(Collections.singleton(recipe));
            });
            recipeManager.byKey(new ResourceLocation(Subservermod.MOD_ID, "cream_bread_recipe")).ifPresent(recipe -> {
                player.awardRecipes(Collections.singleton(recipe));
            });
            recipeManager.byKey(new ResourceLocation(Subservermod.MOD_ID, "apple_diffuser_recipe")).ifPresent(recipe -> {
                player.awardRecipes(Collections.singleton(recipe));
            });
        }
    }
}
