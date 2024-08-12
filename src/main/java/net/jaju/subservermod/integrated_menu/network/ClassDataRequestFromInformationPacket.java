package net.jaju.subservermod.integrated_menu.network;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.subclass.BaseClass;
import net.jaju.subservermod.subclass.ClassManagement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.function.Supplier;

public class ClassDataRequestFromInformationPacket {

    public ClassDataRequestFromInformationPacket() {
    }

    public ClassDataRequestFromInformationPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Map<String, BaseClass> classHashMap = ClassManagement.getClasses(player.getName().getString());
                ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ClassDataInformationSyncPacket(classHashMap));
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
