package net.jaju.subservermod.network.integrated_menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TemporaryOpPacket {
    private final boolean giveOp;

    public TemporaryOpPacket(boolean giveOp) {
        this.giveOp = giveOp;
    }

    public TemporaryOpPacket(FriendlyByteBuf buf) {
        this.giveOp = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(giveOp);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                if (giveOp) {
                    player.server.getPlayerList().op(player.getGameProfile());
                } else {
                    player.server.getPlayerList().deop(player.getGameProfile());
                }
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}