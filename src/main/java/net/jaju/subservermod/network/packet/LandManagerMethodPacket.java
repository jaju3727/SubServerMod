package net.jaju.subservermod.network.packet;

import net.jaju.subservermod.landsystem.LandManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class LandManagerMethodPacket {
    private final String chunkKey;
    private final String type;
    private final UUID playerUUID;


    public LandManagerMethodPacket(String chunkKey, UUID playerUUID, String type) {
        this.chunkKey = chunkKey;
        this.type = type;
        this.playerUUID = playerUUID;
    }

    public LandManagerMethodPacket(String chunkKey, String type) {
        this.chunkKey = chunkKey;
        this.type = type;
        this.playerUUID = new UUID(0, 0);
    }

    public LandManagerMethodPacket(FriendlyByteBuf buf) {
        this.chunkKey = buf.readUtf(32767);
        this.type = buf.readUtf(32767);
        this.playerUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(chunkKey);
        buf.writeUtf(type);
        buf.writeUUID(playerUUID);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                switch (type) {
                    case "removeChunkOwner":
                        LandManager.getInstance().removeChunkOwner(chunkKey);
                        break;
                    case "removeChunkSharer":
                        LandManager.getInstance().removeChunkSharer(chunkKey, playerUUID);
                        break;
                    case "addChunkSharer":
                        LandManager.getInstance().addSharer(chunkKey, playerUUID);
                        break;
                    default:
                        player.sendSystemMessage(Component.literal("알 수 없는 명령: " + type));
                        break;
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
