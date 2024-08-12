package net.jaju.subservermod.encyclopedia.network;

import net.jaju.subservermod.encyclopedia.EncyclopediaManager;
import net.jaju.subservermod.mailbox.MailboxManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class giftGetPacket {
    private final int num;

    public giftGetPacket(int num) {
        this.num = num;
    }

    public static giftGetPacket decode(FriendlyByteBuf buf) {
        int num = buf.readInt();
        return new giftGetPacket(num);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(num);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                LinkedHashMap<Integer, List<ItemStack>> giftList = EncyclopediaManager.getGiftList();
                for (ItemStack itemStack : giftList.get(num)) {
                    MailboxManager.getInstance().addItemToMailbox(player.getUUID(), itemStack);
                }
                EncyclopediaManager.getInstance().getGift(player.getUUID(), num);
            }
        });
        context.get().setPacketHandled(true);
    }
}