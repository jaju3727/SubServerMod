package net.jaju.subservermod.landsystem.network.packet;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class PlayerNamePacket {
    private final String playerName;
    private final UUID playerUUID;
    private final boolean isRequest;

    public PlayerNamePacket(String playerName) {
        this.playerName = playerName;
        this.playerUUID = null;
        this.isRequest = true;
    }

    public PlayerNamePacket(UUID playerUUID) {
        this.playerName = null;
        this.playerUUID = playerUUID;
        this.isRequest = false;
    }

    public PlayerNamePacket(FriendlyByteBuf buf) {
        this.isRequest = buf.readBoolean();
        if (isRequest) {
            this.playerName = buf.readUtf(32767);
            this.playerUUID = null;
        } else {
            this.playerUUID = buf.readUUID();
            this.playerName = null;
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isRequest);
        if (isRequest) {
            buf.writeUtf(playerName);
        } else {
            buf.writeUUID(playerUUID);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer sender = context.get().getSender();
            if (sender != null) {
                if (isRequest) {
                    ServerPlayer targetPlayer = sender.getServer().getPlayerList().getPlayerByName(playerName);
                    if (targetPlayer != null) {
                        UUID targetUUID = targetPlayer.getUUID();
                        sender.sendSystemMessage(Component.literal(targetUUID.toString() + "  " + targetPlayer));
                        ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new PlayerNameResponsePacket(targetUUID));
                    } else {
                        sender.sendSystemMessage(Component.literal("Player not found: " + playerName));
                    }

                } else {
                    String playerName = PlayerUtils.getPlayerNameByUUID(sender.getServer(), playerUUID);
                    if (playerName != null) {
                        sender.sendSystemMessage(Component.literal(playerName));
                        ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new PlayerNameResponsePacket(playerUUID, playerName));
                    } else {
                        sender.sendSystemMessage(Component.literal("Player not found: " + playerUUID));
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
