package net.jaju.subservermod.landsystem.network.packet;

import net.jaju.subservermod.landsystem.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class ChunkOwnersPacket {
    private final LinkedHashMap<String, UUID> chunkOwners;

    public ChunkOwnersPacket(LinkedHashMap<String, UUID> chunkOwners) {
        this.chunkOwners = chunkOwners;
    }

    public static void encode(ChunkOwnersPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.chunkOwners.size());
        msg.chunkOwners.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeUUID(value);
        });
    }

    public static ChunkOwnersPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        LinkedHashMap<String, UUID> chunkOwners = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            chunkOwners.put(buf.readUtf(), buf.readUUID());
        }
        return new ChunkOwnersPacket(chunkOwners);
    }

    public static void handle(ChunkOwnersPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler.handleChunkOwnersPacket(msg.chunkOwners);
        });
        ctx.get().setPacketHandled(true);
    }
}