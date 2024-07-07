package net.jaju.subservermod.shopsystem.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateInventoryPacket {
    private final int slotIndex;
    private final ItemStack itemStack;

    public UpdateInventoryPacket(int slotIndex, ItemStack itemStack) {
        this.slotIndex = slotIndex;
        this.itemStack = itemStack;
    }

    public UpdateInventoryPacket(FriendlyByteBuf buf) {
        this.slotIndex = buf.readInt();
        this.itemStack = buf.readItem();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(slotIndex);
        buf.writeItem(itemStack);
    }

    public static void handle(UpdateInventoryPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getInventory().setItem(packet.slotIndex, packet.itemStack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
