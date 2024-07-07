package net.jaju.subservermod.landsystem.network.packet;

import net.jaju.subservermod.landsystem.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PlayerNameResponsePacket {
    private final UUID playerUUID;
    private final String playerName;
    private final boolean isRequest;

    public PlayerNameResponsePacket(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.isRequest = true;
    }

    public PlayerNameResponsePacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.playerName = "";
        this.isRequest = false;
    }

    public PlayerNameResponsePacket(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
        this.playerName = buf.readUtf(32767);
        this.isRequest = buf.readBoolean();

    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeUtf(playerName);
        buf.writeBoolean(isRequest);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        if (!isRequest) {
            context.get().enqueueWork(() -> {
                ClientPacketHandler.playerNamePacket(playerUUID);
            });
            context.get().setPacketHandled(true);
        }
        else {
            context.get().enqueueWork(() -> {
                ClientPacketHandler.handlePlayerNameResponse(playerUUID, playerName);
            });
            context.get().setPacketHandled(true);
        }

    }
}
