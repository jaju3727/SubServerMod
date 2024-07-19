package net.jaju.subservermod.subclass;

import net.jaju.subservermod.subclass.skill.fisherman.FishermanSkill;
import net.minecraft.server.level.ServerPlayer;

public class Fisherman extends BaseClass {
    private FishermanSkill fishermanSkill;

    public Fisherman(int level, String playerName) {
        super("Fisherman", level, playerName);

        fishermanSkill = new FishermanSkill(this);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        // Fisherman의 스킬 구현
        System.out.println("Performing Fisherman skill at level " + getLevel());
    }
}
