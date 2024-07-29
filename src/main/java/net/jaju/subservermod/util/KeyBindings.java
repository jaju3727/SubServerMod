package net.jaju.subservermod.util;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping openCustomScreenKey = new KeyMapping(
            "SubServerMod_Key",
            GLFW.GLFW_KEY_I,
            "섭이의 방학"
    );
    public static final KeyMapping TestKey = new KeyMapping(
            "나무꾼 추가 인벤토리",
            GLFW.GLFW_KEY_P,
            "섭이의 방학"
    );
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(openCustomScreenKey);
        event.register(TestKey);
    }
}
