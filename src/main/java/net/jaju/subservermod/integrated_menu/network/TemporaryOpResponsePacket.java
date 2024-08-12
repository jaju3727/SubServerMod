package net.jaju.subservermod.integrated_menu.network;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class TemporaryOpResponsePacket {
    private final boolean removeOp;

    public TemporaryOpResponsePacket(boolean removeOp) {
        this.removeOp = removeOp;
    }

    public TemporaryOpResponsePacket(FriendlyByteBuf buf) {
        this.removeOp = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(removeOp);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && removeOp) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Subservermod.LOGGER.info("fbdhjksbfjkhsdhkjfbdsjbf");
                        ModNetworking.INSTANCE.sendToServer(new TemporaryOpPacket(false));
                    }
                }, 1000); // 1000 밀리초 = 1초 후에 권한 제거
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}