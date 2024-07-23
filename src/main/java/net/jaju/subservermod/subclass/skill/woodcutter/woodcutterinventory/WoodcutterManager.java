package net.jaju.subservermod.subclass.skill.woodcutter.woodcutterinventory;

import net.jaju.subservermod.subclass.Woodcutter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WoodcutterManager {
    private static final Map<UUID, Woodcutter> woodcutterMap = new HashMap<>();

    public static Woodcutter getOrCreateWoodcutter(ServerPlayer player) {
        return woodcutterMap.computeIfAbsent(player.getUUID(), uuid -> new Woodcutter(0, player.getName().getString()));
    }

    public static void saveWoodcutterData(ServerPlayer player) {
        Woodcutter woodcutter = woodcutterMap.get(player.getUUID());
        if (woodcutter != null) {
            CompoundTag tag = woodcutter.serializeNBT();
            player.getPersistentData().put("WoodcutterData", tag);
        }
    }

    public static void loadWoodcutterData(ServerPlayer player) {
        Woodcutter woodcutter = getOrCreateWoodcutter(player);
        CompoundTag tag = player.getPersistentData().getCompound("WoodcutterData");
        woodcutter.deserializeNBT(tag);
    }
}