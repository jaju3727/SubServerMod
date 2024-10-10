package net.jaju.subservermod.network.village;

import net.jaju.subservermod.events.VillageEventHandlers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class VillageHudPacket {
    private final String villageName;

    public VillageHudPacket(String villageName) {
        this.villageName = villageName;
    }

    public VillageHudPacket(FriendlyByteBuf buf) {
        this.villageName = buf.readUtf(256);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.villageName);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                UUID playerUUID = Minecraft.getInstance().player.getUUID();
                VillageEventHandlers.showVillageImage(playerUUID, villageName);
            }
        });
        context.setPacketHandled(true);
    }
}
