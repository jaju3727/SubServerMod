package net.jaju.subservermod.mailbox.network;

import net.jaju.subservermod.mailbox.MailboxManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AddItemToMailboxPacket {
    private final UUID playerUUID;
    private final ItemStack itemStack;

    public AddItemToMailboxPacket(UUID playerUUID, ItemStack itemStack) {
        this.playerUUID = playerUUID;
        this.itemStack = itemStack;
    }

    public AddItemToMailboxPacket(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
        this.itemStack = buf.readItem();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeItem(itemStack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                MailboxManager.getInstance().addItemToMailbox(playerUUID, itemStack);
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
