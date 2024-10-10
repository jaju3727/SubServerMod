package net.jaju.subservermod.network.entity.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerEntityPositionPacket {
    private final int entityId;
    private final double x, y, z;
    private final float yaw;

    public PlayerEntityPositionPacket(int entityId, double x, double y, double z, float yaw) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
    }

    public static void encode(PlayerEntityPositionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityId);
        buffer.writeDouble(packet.x);
        buffer.writeDouble(packet.y);
        buffer.writeDouble(packet.z);
        buffer.writeFloat(packet.yaw);
    }

    public static PlayerEntityPositionPacket decode(FriendlyByteBuf buffer) {
        return new PlayerEntityPositionPacket(buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat());
    }

    public static void handle(PlayerEntityPositionPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // 클라이언트 측에서 엔티티 위치 업데이트
            var player = Minecraft.getInstance().player;
            if (player != null && player.level() != null) {
                var entity = player.level().getEntity(packet.entityId);
                if (entity != null) {
                    entity.setPos(packet.x, packet.y, packet.z);
                    entity.setYRot(packet.yaw);
                    entity.setYHeadRot(packet.yaw);
                    entity.setYBodyRot(packet.yaw);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}