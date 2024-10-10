package net.jaju.subservermod.network.mmoclass.packet;

import net.jaju.subservermod.manager.MMOClassManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClassDataSyncPacket {
    private final UUID playerUUID;
    private final String playerClass;
    private final int playerLevel;

    public ClassDataSyncPacket(UUID playerUUID, String playerClass, int playerLevel) {
        this.playerUUID = playerUUID;
        this.playerClass = playerClass;
        this.playerLevel = playerLevel;
    }

    public ClassDataSyncPacket(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
        this.playerClass = buf.readUtf(32767);
        this.playerLevel = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeUtf(playerClass);
        buf.writeInt(playerLevel);
    }

    public static void handle(ClassDataSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                MMOClassManager classManager = MMOClassManager.getInstance();
                classManager.setPlayerClass(message.playerUUID, message.playerClass);
                classManager.setPlayerLevel(message.playerUUID, message.playerLevel);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
