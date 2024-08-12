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

    public TemporaryOpRequestPacket() {}

    public TemporaryOpRequestPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                boolean isOp = player.getServer().getPlayerList().isOp(player.getGameProfile());
                Subservermod.LOGGER.info(String.valueOf(isOp));
//                ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new TemporaryOpResponsePacket(!isOp));
                if (!isOp) {
                    player.getServer().getPlayerList().op(player.getGameProfile());
                    CommandExecutor.executeCommand(player, "/mcmmogui");
                    player.getServer().getPlayerList().deop(player.getGameProfile());
                    return;
                }
                CommandExecutor.executeCommand(player, "/mcmmogui");

            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}