package net.jaju.subservermod.integrated_menu.network;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.util.CommandExecutor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class TemporaryOpRequestPacket {

    private final String command;

    public TemporaryOpRequestPacket(String command) {
        this.command = command;
    }

    public TemporaryOpRequestPacket(FriendlyByteBuf buf) {
        this.command = buf.readUtf(32767); // 최대 32767 길이의 문자열을 읽음
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.command);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                boolean isOp = player.getServer().getPlayerList().isOp(player.getGameProfile());
                if (!isOp) {
                    player.getServer().getPlayerList().op(player.getGameProfile());
                    CommandExecutor.executeCommand(player, this.command);
                    player.getServer().getPlayerList().deop(player.getGameProfile());
                    return;
                }
                CommandExecutor.executeCommand(player, this.command);
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}