package net.jaju.subservermod.subclass;

import net.minecraft.server.level.ServerPlayer;

public abstract class BaseClass {
    private final String name;
    private final int level;
    private String playerName;

    public BaseClass(String name, int level, String playerName) {
        this.name = name;
        this.level = level;
        this.playerName = playerName;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public abstract void performSkill(String skillName, ServerPlayer player);
}
