package net.jaju.subservermod.subclass;

import net.jaju.subservermod.subclass.skill.woodcutter.WoodcutterSkill;
import net.minecraft.server.level.ServerPlayer;

public class Woodcutter extends BaseClass {
    private transient WoodcutterSkill woodcutterSkill;

    public Woodcutter(int level, String playerName) {
        super("Woodcutter", level, playerName);
        woodcutterSkill = new WoodcutterSkill(this);

    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        // Woodcutter의 스킬 구현
        System.out.println("Performing Woodcutter skill at level " + getLevel());

    }
}
