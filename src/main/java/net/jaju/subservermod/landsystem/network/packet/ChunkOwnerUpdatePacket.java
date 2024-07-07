package net.jaju.subservermod.landsystem.network.packet;

import net.jaju.subservermod.landsystem.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class ChunkOwnerUpdatePacket {
    private final UUID playerUUID;
    private final UUID ownerUUID;

    public ChunkOwnerUpdatePacket(UUID playerUUID, UUID ownerUUID) {
        this.playerUUID = playerUUID;
        this.ownerUUID = ownerUUID;
    }

    public static void encode(ChunkOwnerUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeBoolean(msg.ownerUUID != null);
        if (msg.ownerUUID != null) {
            buf.writeUUID(msg.ownerUUID);
        }
    }

    public static ChunkOwnerUpdatePacket decode(FriendlyByteBuf buf) {
        UUID playerUUID = buf.readUUID();
        UUID ownerUUID = buf.readBoolean() ? buf.readUUID() : null;
        return new ChunkOwnerUpdatePacket(playerUUID, ownerUUID);
    }

    public static void handle(ChunkOwnerUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler.handleChunkOwnerUpdatePacket(msg.playerUUID, msg.ownerUUID);
        });
        ctx.get().setPacketHandled(true);
    }
}

