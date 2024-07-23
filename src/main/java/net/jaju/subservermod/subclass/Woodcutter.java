package net.jaju.subservermod.subclass;

import net.jaju.subservermod.subclass.skill.woodcutter.WoodcutterSkill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public class Woodcutter extends BaseClass implements INBTSerializable<CompoundTag> {
    private transient WoodcutterSkill woodcutterSkill;
    private transient ServerPlayer player;
    private ItemStack[] extraInventory;
    private static final int EXTRA_SLOTS = 9;

    public Woodcutter(int level, String playerName) {
        super("Woodcutter", level, playerName);
        this.extraInventory = new ItemStack[EXTRA_SLOTS];
        for (int i = 0; i < EXTRA_SLOTS; i++) {
            this.extraInventory[i] = ItemStack.EMPTY;
        }
        woodcutterSkill = new WoodcutterSkill(this);
    }

    public ItemStack[] getExtraInventory() {
        return extraInventory;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < extraInventory.length; i++) {
            tag.put("slot_" + i, extraInventory[i].save(new CompoundTag()));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        for (int i = 0; i < extraInventory.length; i++) {
            extraInventory[i] = ItemStack.of(tag.getCompound("slot_" + i));
        }
    }

    @Override
    public void performSkill(String skillName, ServerPlayer player) {
        this.player = player;
        System.out.println("Performing Woodcutter skill at level " + getLevel());
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
