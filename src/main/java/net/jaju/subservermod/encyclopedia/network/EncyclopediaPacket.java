package net.jaju.subservermod.encyclopedia.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class EncyclopediaPacket {
    private final LinkedHashMap<String, Integer> encyclopedia;
    private final HashMap<String, Boolean> discoveries;

    public EncyclopediaPacket(LinkedHashMap<String, Integer> encyclopedia, HashMap<String, Boolean> discoveries) {
        this.discoveries = discoveries;
        this.encyclopedia = encyclopedia;
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
    }

    public static EncyclopediaPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        LinkedHashMap<String, Integer> encyclopedia = new LinkedHashMap<>();
        HashMap<String, Boolean> discoveries = new HashMap<>();
        for (int i = 0; i < size; i++) {
            discoveries.put(buf.readUtf(), buf.readBoolean());
        }
        size = buf.readInt();
        for (int i = 0; i < size; i++) {
            encyclopedia.put(buf.readUtf(), buf.readInt());
        }
        return new EncyclopediaPacket(encyclopedia, discoveries);
    }

    public static void handle(EncyclopediaPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler.handleEncyclopediaDataPacket(msg.encyclopedia, msg.discoveries);
        });
        ctx.get().setPacketHandled(true);
    }
}