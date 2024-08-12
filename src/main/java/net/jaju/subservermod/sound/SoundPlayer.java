package net.jaju.subservermod.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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

    private static final Map<UUID, ResourceLocation> activeSounds = new HashMap<>();
    private static final Map<UUID, Vec3> soundPositions = new HashMap<>();
    private static final Map<UUID, Double> soundRadii = new HashMap<>();

    public static void playCustomSound(Player player, ResourceLocation soundLocation, float volume, float pitch) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundLocation);
        if (soundEvent != null) {
            playSound(player, soundLocation, volume, pitch);
        } else {
            player.sendSystemMessage(Component.literal("사운드 이벤트를 찾을 수 없습니다: " + soundLocation));
        }
    }

    private static void playSound(Player player, ResourceLocation soundLocation, float volume, float pitch) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundLocation);
        if (soundEvent != null) {
            Holder<SoundEvent> soundEventHolder = Holder.direct(soundEvent);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSoundPacket(soundEventHolder, SoundSource.PLAYERS, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), volume, pitch, serverPlayer.level().getRandom().nextLong()));
                activeSounds.put(player.getUUID(), soundLocation);
            } else if (player instanceof LocalPlayer localPlayer) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, volume, pitch));
                activeSounds.put(player.getUUID(), soundLocation);
            }
        }
    }

    public static void playCustomSoundInRadius(Level level, Vec3 pos, double radius, ResourceLocation soundLocation, float volume, float pitch) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundLocation);
        if (soundEvent != null) {
            playSoundInRadius(level, pos, radius, soundLocation, volume, pitch);
        }
    }

    private static void playSoundInRadius(Level level, Vec3 pos, double radius, ResourceLocation soundLocation, float volume, float pitch) {
        AABB aabb = new AABB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius);
        List<Player> players = level.getEntitiesOfClass(Player.class, aabb);

        for (Player player : players) {
            playSound(player, soundLocation, volume, pitch);
            soundPositions.put(player.getUUID(), pos);
            soundRadii.put(player.getUUID(), radius);
        }
    }
}
