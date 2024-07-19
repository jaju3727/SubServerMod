package net.jaju.subservermod.subclass;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.skill.miner.MinerSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Collections;

public class Miner extends BaseClass {
    private transient MinerSkill minerSkill;

    public Miner(int level, String playerName) {
        super("Miner", level, playerName);
        minerSkill = new MinerSkill(this);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        if (getLevel() == 2) {
            RecipeManager recipeManager = player.server.getRecipeManager();

            recipeManager.byKey(new ResourceLocation(Subservermod.MOD_ID, "crafting_tool_recipe")).ifPresent(recipe -> {
                player.awardRecipes(Collections.singleton(recipe));
            });
        }
    }
}
