package net.jaju.subservermod.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class PlayerUtils {

    /**
     * UUID로 플레이어의 닉네임을 찾는 메서드
     *
     * @param server MinecraftServer 인스턴스
     * @param playerUUID 플레이어의 UUID
     * @return 플레이어의 닉네임 (존재하지 않는 경우 null)
     */
    public static String getPlayerNameByUUID(MinecraftServer server, UUID playerUUID) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);
        if (player != null) {
            return player.getGameProfile().getName();
        } else {
            return null;
        }
    }
}
