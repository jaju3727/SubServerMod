package net.jaju.subservermod.subclass.network;

import net.jaju.subservermod.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GaugeSendToEntityPacket {
    private final BlockPos pos;
    private final int gaugeX;

    public GaugeSendToEntityPacket(BlockPos pos, int gaugeX) {
        this.pos = pos;
        this.gaugeX = gaugeX;
    }

    public static void encode(GaugeSendToEntityPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeInt(packet.gaugeX);
    }

    public static GaugeSendToEntityPacket decode(FriendlyByteBuf buffer) {
        return new GaugeSendToEntityPacket(buffer.readBlockPos(), buffer.readInt());
    }

    public static void handle(GaugeSendToEntityPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.level().getBlockEntity(packet.pos, ModBlockEntities.WOODCUTTINGUNION_BLOCK_ENTITY.get())
                        .ifPresent(blockEntity -> blockEntity.updateGaugeX(packet.gaugeX));
            }
        });
        context.setPacketHandled(true);
    }
}
