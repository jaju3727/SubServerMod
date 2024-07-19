package net.jaju.subservermod.subclass;

import net.jaju.subservermod.subclass.skill.chef.ChefSkill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Chef extends BaseClass {
    private transient ChefSkill chefSkill;

    public Chef(int level, String playerName) {
        super("Chef", level, playerName);
        chefSkill = new ChefSkill(this);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        if(getLevel() >= 2) {
            AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
            if (attribute != null) {
                attribute.setBaseValue(30);
            }
        }
    }
}
