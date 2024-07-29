package net.jaju.subservermod.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SoundPlayer {

    private static final Map<UUID, Map<ResourceLocation, Long>> playerSoundCooldowns = new HashMap<>();
    private static final long DEFAULT_SOUND_COOLDOWN_MS = 5000; // 기본 쿨다운 시간: 5초

    public static void playCustomSound(Player player, ResourceLocation soundLocation, float volume, float pitch) {
        playCustomSound(player, soundLocation, volume, pitch, DEFAULT_SOUND_COOLDOWN_MS);
    }

    public static void playCustomSound(Player player, ResourceLocation soundLocation, float volume, float pitch, long cooldownMs) {
        long currentTime = System.currentTimeMillis();
        UUID playerUUID = player.getUUID();

        playerSoundCooldowns.putIfAbsent(playerUUID, new HashMap<>());
        Map<ResourceLocation, Long> soundCooldowns = playerSoundCooldowns.get(playerUUID);

        if (canPlaySound(soundCooldowns, soundLocation, currentTime, cooldownMs)) {
            SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundLocation);
            if (soundEvent != null) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.level().playSound(null, serverPlayer.blockPosition(), soundEvent, SoundSource.PLAYERS, volume, pitch);
                } else if (player instanceof LocalPlayer localPlayer) {
                    Minecraft.getInstance().level.playLocalSound(localPlayer.getX(), localPlayer.getY(), localPlayer.getZ(), soundEvent, SoundSource.PLAYERS, volume, pitch, false);
                }
                soundCooldowns.put(soundLocation, currentTime);
            } else {
                player.sendSystemMessage(Component.literal("사운드 이벤트를 찾을 수 없습니다: " + soundLocation));
            }
        } else {
            player.sendSystemMessage(Component.literal("이 소리는 현재 재생할 수 없습니다. 잠시 후 다시 시도하세요."));
        }
    }

    public static void playCustomSoundInRadius(Level level, Vec3 pos, double radius, String soundPath) {
        playCustomSoundInRadius(level, pos, radius, soundPath, DEFAULT_SOUND_COOLDOWN_MS);
    }

    public static void playCustomSoundInRadius(Level level, Vec3 pos, double radius, String soundPath, long cooldownMs) {
        ResourceLocation soundLocation = new ResourceLocation(soundPath);
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundLocation);
        if (soundEvent != null) {
            AABB aabb = new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);
            List<Player> players = level.getEntitiesOfClass(Player.class, aabb);
            long currentTime = System.currentTimeMillis();

            for (Player player : players) {
                UUID playerUUID = player.getUUID();
                playerSoundCooldowns.putIfAbsent(playerUUID, new HashMap<>());
                Map<ResourceLocation, Long> soundCooldowns = playerSoundCooldowns.get(playerUUID);

                if (canPlaySound(soundCooldowns, soundLocation, currentTime, cooldownMs)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    } else if (player instanceof LocalPlayer localPlayer) {
                        Minecraft.getInstance().level.playLocalSound(localPlayer.getX(), localPlayer.getY(), localPlayer.getZ(), soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F, false);
                    }
                    soundCooldowns.put(soundLocation, currentTime);
                }
            }
        }
    }

    private static boolean canPlaySound(Map<ResourceLocation, Long> soundCooldowns, ResourceLocation soundLocation, long currentTime, long cooldownMs) {
        return !soundCooldowns.containsKey(soundLocation) || (currentTime - soundCooldowns.get(soundLocation)) > cooldownMs;
    }
}