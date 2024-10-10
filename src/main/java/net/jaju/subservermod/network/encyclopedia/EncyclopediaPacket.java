package net.jaju.subservermod.network.encyclopedia;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class EncyclopediaPacket {
    private final LinkedHashMap<String, Integer> encyclopedia;
    private final HashMap<String, Boolean> discoveries;
    private final LinkedHashMap<Integer, List<ItemStack>> giftList;
    private final LinkedHashMap<Integer, Boolean> giftGet;

    public EncyclopediaPacket(LinkedHashMap<String, Integer> encyclopedia, HashMap<String, Boolean> discoveries, LinkedHashMap<Integer, List<ItemStack>> giftList, LinkedHashMap<Integer, Boolean> giftGet) {
        this.discoveries = discoveries;
        this.encyclopedia = encyclopedia;
        this.giftList = giftList;
        this.giftGet = giftGet;
    }

    public static void encode(EncyclopediaPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.discoveries.size());
        msg.discoveries.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeBoolean(value);
        });
        buf.writeInt(msg.encyclopedia.size());
        msg.encyclopedia.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeInt(value);
        });
        buf.writeInt(msg.giftGet.size());
        msg.giftGet.forEach((key, value) -> {
            buf.writeInt(key);
            buf.writeBoolean(value);
        });
        buf.writeInt(msg.giftList.size());
        msg.giftList.forEach((key, value) -> {
            buf.writeInt(value.size());
            for (ItemStack itemStack : value) buf.writeItem(itemStack);
            buf.writeInt(key);
        });
    }

    public static EncyclopediaPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        LinkedHashMap<String, Integer> encyclopedia = new LinkedHashMap<>();
        LinkedHashMap<Integer, List<ItemStack>> gitfList = new LinkedHashMap<>();
        HashMap<String, Boolean> discoveries = new HashMap<>();
        LinkedHashMap<Integer, Boolean> giftGet = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            discoveries.put(buf.readUtf(), buf.readBoolean());
        }
        size = buf.readInt();
        for (int i = 0; i < size; i++) {
            encyclopedia.put(buf.readUtf(), buf.readInt());
        }
        size = buf.readInt();
        for (int i = 0; i < size; i++) {
            giftGet.put(buf.readInt(), buf.readBoolean());
        }
        size = buf.readInt();
        for (int i = 0; i < size; i++) {
            int Size = buf.readInt();
            List<ItemStack> list = new ArrayList<>();
            for(int j = 0; j < Size; j++) {
                list.add(buf.readItem());
            }
            gitfList.put(buf.readInt(), list);
        }

        return new EncyclopediaPacket(encyclopedia, discoveries, gitfList, giftGet);
    }

    public static void handle(EncyclopediaPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler.handleEncyclopediaDataPacket(msg.encyclopedia, msg.discoveries, msg.giftList, msg.giftGet);
        });
        ctx.get().setPacketHandled(true);
    }
}