package net.jaju.subservermod.subclass.network;

import net.jaju.subservermod.subclass.skill.woodcutter.woodcuttingunion.WoodcuttingUnionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GaugeSendToClientPacket {
    private final int gaugeX;
    private final boolean flag;
    private final int minItemCount;

    public GaugeSendToClientPacket(int gaugeX, boolean flag, int minItemCount) {
        this.gaugeX = gaugeX;
        this.flag = flag;
        this.minItemCount = minItemCount;
    }

    public static void encode(GaugeSendToClientPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.gaugeX);
        buffer.writeBoolean(packet.flag);
        buffer.writeInt(packet.minItemCount);
    }

    public static GaugeSendToClientPacket decode(FriendlyByteBuf buffer) {
        return new GaugeSendToClientPacket(buffer.readInt(), buffer.readBoolean(), buffer.readInt());
    }

    public static void handle(GaugeSendToClientPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof WoodcuttingUnionScreen screen) {
                screen.updateVar(packet.gaugeX, packet.flag, packet.minItemCount);
            }
        });
        context.setPacketHandled(true);
    }
}
