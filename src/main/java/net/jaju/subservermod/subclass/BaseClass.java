package net.jaju.subservermod.subclass;

import net.minecraft.network.FriendlyByteBuf;
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

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeInt(level);
        buf.writeUtf(playerName);
    }

    public static BaseClass fromBytes(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        int level = buf.readInt();
        String playerName = buf.readUtf();
        return new SubClass(name, level, playerName);
    }
}

class SubClass extends BaseClass {
    public SubClass(String name, int level, String playerName) {
        super(name, level, playerName);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        // Implement skill logic here
    }
}
