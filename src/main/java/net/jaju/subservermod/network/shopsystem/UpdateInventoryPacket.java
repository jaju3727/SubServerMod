package net.jaju.subservermod.network.shopsystem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateInventoryPacket {
    private final int slotIndex;
    private final ItemStack itemStack;
    private final UUID playerUUID;

    public UpdateInventoryPacket(int slotIndex, ItemStack itemStack) {
        this.slotIndex = slotIndex;
        this.itemStack = itemStack;
        this.playerUUID = new UUID(1, 1);
    }

    public UpdateInventoryPacket(int slotIndex, ItemStack itemStack, UUID playerUUID) {
        this.slotIndex = slotIndex;
        this.itemStack = itemStack;
        this.playerUUID = playerUUID;
    }

    public UpdateInventoryPacket(FriendlyByteBuf buf) {
        this.slotIndex = buf.readInt();
        this.itemStack = buf.readItem();
        this.playerUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(slotIndex);
        buf.writeItem(itemStack);
        buf.writeUUID(playerUUID);
    }

    public static void handle(UpdateInventoryPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player;
            if (packet.playerUUID.equals(new UUID(1, 1))) {
                player = ctx.get().getSender();
            } else {
                MinecraftServer server = ctx.get().getSender().server;
                player = server.getPlayerList().getPlayer(packet.playerUUID);
            }

            if (player != null) {
                player.getInventory().setItem(packet.slotIndex, packet.itemStack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
