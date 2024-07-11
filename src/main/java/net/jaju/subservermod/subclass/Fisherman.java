package net.jaju.subservermod.subclass;

import net.minecraft.server.level.ServerPlayer;

public class Fisherman extends BaseClass {
    public Fisherman(int level, String playerName) {
        super("Fisherman", level, playerName);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        // Fisherman의 스킬 구현
        System.out.println("Performing Fisherman skill at level " + getLevel());
    }
}
