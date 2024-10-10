package net.jaju.subservermod.network.mmoclass.packet;

import net.jaju.subservermod.manager.MMOClassManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncClassDataPacket {
    private final UUID playerUUID;
    private final String playerClass;
    private final int playerLevel;

    public SyncClassDataPacket(UUID playerUUID, String playerClass, int playerLevel) {
        this.playerUUID = playerUUID;
        this.playerClass = playerClass;
        this.playerLevel = playerLevel;
    }

    public SyncClassDataPacket(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
        this.playerClass = buf.readUtf();
        this.playerLevel = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeUtf(playerClass);
        buf.writeInt(playerLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 클라이언트 측에서 플레이어의 클래스와 레벨 업데이트
            MMOClassManager classManager = MMOClassManager.getInstance();
            classManager.setPlayerClass(playerUUID, playerClass);
            classManager.setPlayerLevel(playerUUID, playerLevel);
        });
        ctx.get().setPacketHandled(true);
    }
}