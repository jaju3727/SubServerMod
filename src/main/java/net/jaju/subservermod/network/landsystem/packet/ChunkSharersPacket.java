package net.jaju.subservermod.network.landsystem.packet;

import net.jaju.subservermod.network.landsystem.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ChunkSharersPacket {
    private final LinkedHashMap<String, List<UUID>> chunkSharers;

    public ChunkSharersPacket(LinkedHashMap<String, List<UUID>> chunkSharers) {
        this.chunkSharers = chunkSharers;
    }

    public static void encode(ChunkSharersPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.chunkSharers.size());
        msg.chunkSharers.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeInt(value.size());
            for (UUID uuid : value) {
                buf.writeUUID(uuid);
            }
        });
    }

    public static ChunkSharersPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        LinkedHashMap<String, List<UUID>> chunkSharers = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            String key = buf.readUtf();
            int listSize = buf.readInt();
            List<UUID> value = new ArrayList<>();
            for (int j = 0; j < listSize; j++) {
                value.add(buf.readUUID());
            }
            chunkSharers.put(key, value);
        }
        return new ChunkSharersPacket(chunkSharers);
    }

    public static void handle(ChunkSharersPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler.handleChunkSharersPacket(msg.chunkSharers);
        });
        ctx.get().setPacketHandled(true);
    }
}