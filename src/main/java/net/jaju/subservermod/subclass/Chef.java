package net.jaju.subservermod.subclass;

import net.minecraft.server.level.ServerPlayer;

public class Chef extends BaseClass {
    public Chef(int level, String playerName) {
        super("Chef", level, playerName);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        // Chef의 스킬 구현
        System.out.println("Performing Chef skill at level " + getLevel());
    }
}
