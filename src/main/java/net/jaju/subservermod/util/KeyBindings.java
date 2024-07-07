package net.jaju.subservermod.util;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping openCustomScreenKey = new KeyMapping(
            "key.subservermod.opencustomscreen",
            GLFW.GLFW_KEY_I,
            "key.categories.subservermod"
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(openCustomScreenKey);
    }
}
