package net.jaju.subservermod.subclass.network;

import net.jaju.subservermod.subclass.skill.alchemist.brewing.BrewingScreen;
import net.jaju.subservermod.subclass.skill.woodcutter.woodcuttingunion.WoodcuttingUnionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BrewingVarSendToClientPacket {
    private final int blazeCount;
    private final int waterCount;

    public BrewingVarSendToClientPacket(int blazeCount, int waterCount) {
        this.blazeCount = blazeCount;
        this.waterCount = waterCount;
    }

    public static void encode(BrewingVarSendToClientPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.blazeCount);
        buffer.writeInt(packet.waterCount);
    }

    public static BrewingVarSendToClientPacket decode(FriendlyByteBuf buffer) {
        return new BrewingVarSendToClientPacket(buffer.readInt(), buffer.readInt());
    }

    public static void handle(BrewingVarSendToClientPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof BrewingScreen screen) {
                screen.updateVar(packet.blazeCount, packet.waterCount);
            }
        });
        context.setPacketHandled(true);
    }
}
