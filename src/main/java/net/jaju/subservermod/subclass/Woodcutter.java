package net.jaju.subservermod.subclass;

import net.jaju.subservermod.subclass.skill.woodcutter.WoodcutterSkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public class Woodcutter extends BaseClass{
    private transient WoodcutterSkill woodcutterSkill;
    private transient ServerPlayer player;

    public Woodcutter(int level, String playerName) {
        super("Woodcutter", level, playerName);
        woodcutterSkill = new WoodcutterSkill(this);
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        this.player = player;
        System.out.println("Performing Woodcutter skill at level " + getLevel());
    }
}
