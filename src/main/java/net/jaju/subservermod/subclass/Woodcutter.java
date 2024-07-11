package net.jaju.subservermod.subclass;

import net.minecraft.server.level.ServerPlayer;

public class Woodcutter extends BaseClass {
    public Woodcutter(int level, String playerName) {
        super("Woodcutter", level, playerName);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        // Woodcutter의 스킬 구현
        System.out.println("Performing Woodcutter skill at level " + getLevel());
    }
}
