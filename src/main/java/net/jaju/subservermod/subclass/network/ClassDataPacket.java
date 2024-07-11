// ClassDataPacket.java
package net.jaju.subservermod.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.jaju.subservermod.subclass.BaseClass;
import net.jaju.subservermod.subclass.BaseClassTypeAdapter;
import net.jaju.subservermod.subclass.ClassManagement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Supplier;

public class ClassDataPacket {
    private final Map<String, Map<String, BaseClass>> classData;

    public ClassDataPacket(Map<String, Map<String, BaseClass>> classData) {
        this.classData = classData;
    }

    public ClassDataPacket(FriendlyByteBuf buf) {
        String json = buf.readUtf();
        Gson gson = new GsonBuilder().registerTypeAdapter(BaseClass.class, new BaseClassTypeAdapter()).create();
        Type type = new TypeToken<Map<String, Map<String, BaseClass>>>() {}.getType();
        this.classData = gson.fromJson(json, type);
    }

    public void toBytes(FriendlyByteBuf buf) {
        Gson gson = new GsonBuilder().registerTypeAdapter(BaseClass.class, new BaseClassTypeAdapter()).create();
        String json = gson.toJson(classData);
        buf.writeUtf(json);
    }

    public static void handle(ClassDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 서버와 클라이언트 사이의 데이터 동기화 처리
            ClassManagement.handleClassDataPacket(packet);
        });
        ctx.get().setPacketHandled(true);
    }
}
