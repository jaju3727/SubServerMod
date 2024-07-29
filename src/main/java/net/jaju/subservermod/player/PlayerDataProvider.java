package net.jaju.subservermod.player;


import net.jaju.subservermod.Subservermod;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class PlayerDataProvider {
    private static final ConcurrentHashMap<UUID, PlayerData> PLAYER_DATA_MAP = new ConcurrentHashMap<>();

    public static PlayerData get(Player player) {
        return PLAYER_DATA_MAP.computeIfAbsent(player.getUUID(), uuid -> new PlayerData(9));
    }

    public static void remove(Player player) {
        PLAYER_DATA_MAP.remove(player.getUUID());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PlayerData original = get(event.getOriginal());
            PlayerData clone = get(event.getEntity());
            if (original != null && clone != null) {
                clone.deserializeNBT(original.serializeNBT());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerData playerData = get(event.getEntity());
        if (playerData != null) {
            playerData.deserializeNBT(event.getEntity().getPersistentData().getCompound(Subservermod.MOD_ID));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerData playerData = get(event.getEntity());
        if (playerData != null) {
            event.getEntity().getPersistentData().put(Subservermod.MOD_ID, playerData.serializeNBT());
        }
        remove(event.getEntity());
    }
}