package net.jaju.subservermod.network.integrated_menu;

import net.jaju.subservermod.util.CommandExecutor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CommandExecutorPacket {
    private final String command;

    public CommandExecutorPacket(String command) {
        this.command = command;
    }

    public static void encode(CommandExecutorPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.command);
    }

    public static CommandExecutorPacket decode(FriendlyByteBuf buffer) {
        return new CommandExecutorPacket(buffer.readUtf());
    }

    public static void handle(CommandExecutorPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                CommandExecutor.executeCommand(player, packet.command);
            }
        });
        context.setPacketHandled(true);
    }
}
