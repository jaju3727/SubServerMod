package net.jaju.subservermod.integrated_menu.network;

import net.jaju.subservermod.integrated_menu.MyInformationScreen;
import net.jaju.subservermod.subclass.BaseClass;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ClassDataInformationSyncPacket {

    private Map<String, BaseClass> classHashMap;

    public ClassDataInformationSyncPacket(Map<String, BaseClass> classHashMap) {
        this.classHashMap = classHashMap;
    }

    public ClassDataInformationSyncPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        classHashMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String key = buf.readUtf();
            BaseClass value = BaseClass.fromBytes(buf);
            classHashMap.put(key, value);
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(classHashMap.size());
        for (Map.Entry<String, BaseClass> entry : classHashMap.entrySet()) {
            buf.writeUtf(entry.getKey());
            entry.getValue().toBytes(buf);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen instanceof MyInformationScreen) {
                ((MyInformationScreen) mc.screen).updateClass(classHashMap);
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
