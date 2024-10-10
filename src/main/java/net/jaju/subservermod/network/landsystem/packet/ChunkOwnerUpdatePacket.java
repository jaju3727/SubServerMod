package net.jaju.subservermod.network.landsystem.packet;

import net.jaju.subservermod.network.landsystem.ClientPacketHandler;
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
    private final String ownerName;

    public ChunkOwnerUpdatePacket(UUID playerUUID, UUID ownerUUID, String ownerName) {
        this.playerUUID = playerUUID;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
    }

    public static void encode(ChunkOwnerUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeBoolean(msg.ownerUUID != null);
        if (msg.ownerUUID != null) {
            buf.writeUUID(msg.ownerUUID);
            buf.writeUtf(msg.ownerName);
        }
    }

    public static ChunkOwnerUpdatePacket decode(FriendlyByteBuf buf) {
        UUID playerUUID = buf.readUUID();
        UUID ownerUUID = null;
        String ownerName = null;
        if (buf.readBoolean()) {
            ownerUUID = buf.readUUID();
            ownerName = buf.readUtf();
        }
        return new ChunkOwnerUpdatePacket(playerUUID, ownerUUID, ownerName);
    }

    public static void handle(ChunkOwnerUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler.handleChunkOwnerUpdatePacket(msg.playerUUID, msg.ownerUUID, msg.ownerName);
        });
        ctx.get().setPacketHandled(true);
    }
}

