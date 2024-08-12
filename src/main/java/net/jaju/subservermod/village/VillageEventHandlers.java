package net.jaju.subservermod.village;

import io.netty.buffer.Unpooled;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.landsystem.LandManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

import static net.jaju.subservermod.landsystem.LandManager.isOwner;
import static net.jaju.subservermod.landsystem.LandManager.isSharedWith;

public class VillageEventHandlers {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();

        if (!player.level().isClientSide && player.level().dimension() == Level.OVERWORLD) {
            if (!VillageProtectionManager.canPlayerModify(player, pos)) {
                player.sendSystemMessage(Component.literal("마을을 망치지 마세요!"));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            BlockPos pos = event.getPos();

            if (player.level().dimension() == Level.OVERWORLD) {
                if (!VillageProtectionManager.canPlayerModify(player, pos)) {
                    player.sendSystemMessage(Component.literal("마을을 해치지 마세요!"));
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event) {
        if (event.getLevel().dimension() == Level.OVERWORLD) {
            Vec3 explosionPosition = event.getExplosion().getPosition();
            BlockPos pos = new BlockPos((int) explosionPosition.x, (int) explosionPosition.y, (int) explosionPosition.z);

            if (VillageProtectionManager.isInProtectedVillage2(pos, event.getLevel())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.level().dimension() == Level.OVERWORLD) {
                BlockPos playerPos = serverPlayer.blockPosition();
                UUID playerUUID = serverPlayer.getUUID();

                String currentVillage = VillageProtectionManager.getVillageAt(playerPos, serverPlayer.level());
                String playerCurrentVillage = VillageProtectionManager.getPlayerVillage(playerUUID);

                if (currentVillage != null && (playerCurrentVillage == null || !playerCurrentVillage.equals(currentVillage))) {
                    VillageProtectionManager.updatePlayerVillage(playerUUID, currentVillage);
                    ModNetworking.sendToClient(new VillageHudPacket(currentVillage), serverPlayer);
                } else if (currentVillage == null && playerCurrentVillage != null) {
                    VillageProtectionManager.removePlayerVillage(playerUUID);
                }
            }
        }
    }
}
