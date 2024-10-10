package net.jaju.subservermod.network.subclass;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClassDataPacket {
    private final String playerName;
    private final String className;
    private final int level;

    public ClassDataPacket(String playerName, String className, int level) {
        this.playerName = playerName;
        this.className = className;
        this.level = level;
    }

    public ClassDataPacket(FriendlyByteBuf buf) {
        this.playerName = buf.readUtf(32767);
        this.className = buf.readUtf(32767);
        this.level = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.playerName);
        buf.writeUtf(this.className);
        buf.writeInt(this.level);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {

            }
        });
        context.setPacketHandled(true);
    }
}
