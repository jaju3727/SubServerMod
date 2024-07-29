package net.jaju.subservermod.subclass;

import net.jaju.subservermod.subclass.skill.alchemist.AlchemistSkill;
import net.jaju.subservermod.util.CommandExecutor;
import net.minecraft.server.level.ServerPlayer;

public class Alchemist extends BaseClass {
    private transient AlchemistSkill alchemistSkill;

    public Alchemist(int level, String playerName) {
        super("Alchemist", level, playerName);
        alchemistSkill = new AlchemistSkill(this);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {

    }
}
