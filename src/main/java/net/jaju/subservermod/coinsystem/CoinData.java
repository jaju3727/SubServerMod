package net.jaju.subservermod.coinsystem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

public class CoinData implements INBTSerializable<CompoundTag> {
    private int subcoin;
    private int chefcoin;
    private int farmercoin;
    private int fishermancoin;
    private int alchemistcoin;
    private int minercoin;
    private int woodcuttercoin;

    public CoinData() {
        this.subcoin = 0;
        this.chefcoin = 0;
        this.farmercoin = 0;
        this.fishermancoin = 0;
        this.alchemistcoin = 0;
        this.minercoin = 0;
        this.woodcuttercoin = 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("subcoin", subcoin);
        tag.putInt("chefcoin", chefcoin);
        tag.putInt("farmercoin", farmercoin);
        tag.putInt("fishermancoin", fishermancoin);
        tag.putInt("alchemistcoin", alchemistcoin);
        tag.putInt("minercoin", minercoin);
        tag.putInt("woodcuttercoin", woodcuttercoin);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.subcoin = nbt.getInt("subcoin");
        this.chefcoin = nbt.getInt("chefcoin");
        this.farmercoin = nbt.getInt("farmercoin");
        this.fishermancoin = nbt.getInt("fishermancoin");
        this.alchemistcoin = nbt.getInt("alchemistcoin");
        this.minercoin = nbt.getInt("ainercoin");
        this.woodcuttercoin = nbt.getInt("woodcuttercoin");
    }

    public int getSubcoin() {
        return subcoin;
    }

    public void setSubcoin(int subcoin) {
        this.subcoin = subcoin;
    }

    public int getChefcoin() {
        return chefcoin;
    }

    public void setChefcoin(int chefcoin) {
        this.chefcoin = chefcoin;
    }

    public int getFarmercoin() {
        return farmercoin;
    }

    public void setFarmercoin(int farmercoin) {
        this.farmercoin = farmercoin;
    }

    public int getFishermancoin() {
        return fishermancoin;
    }

    public void setFishermancoin(int fishermancoin) {
        this.fishermancoin = fishermancoin;
    }

    public int getAlchemistcoin() {
        return alchemistcoin;
    }

    public void setAlchemistcoin(int alchemistcoin) {
        this.alchemistcoin = alchemistcoin;
    }

    public int getMinercoin() {
        return minercoin;
    }

    public void setMinercoin(int minercoin) {
        this.minercoin = minercoin;
    }

    public int getWoodcuttercoin() {
        return woodcuttercoin;
    }

    public void setWoodcuttercoin(int woodcuttercoin) {
        this.woodcuttercoin = woodcuttercoin;
    }

    public void saveToPlayer(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        tag.putInt("subcoin", subcoin);
        tag.putInt("chefcoin", chefcoin);
        tag.putInt("farmercoin", farmercoin);
        tag.putInt("fishermancoin", fishermancoin);
        tag.putInt("alchemistcoin", alchemistcoin);
        tag.putInt("minercoin", minercoin);
        tag.putInt("woodcuttercoin", woodcuttercoin);
    }

    public void loadFromPlayer(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        subcoin = tag.getInt("subcoin");
        chefcoin = tag.getInt("chefcoin");
        farmercoin = tag.getInt("farmercoin");
        fishermancoin = tag.getInt("fishermancoin");
        alchemistcoin = tag.getInt("alchemistcoin");
        minercoin = tag.getInt("minercoin");
        woodcuttercoin = tag.getInt("woodcuttercoin");
    }
}
