package net.jaju.subservermod.util;

import com.google.common.collect.Lists;
import net.jaju.subservermod.Subservermod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KeyBindings {
    private static final List<KeyMapping> keyList = Lists.newArrayList();

    public static final KeyMapping openCustomScreenKey = register(new KeyMapping(
            "key.subservermod.open_custom_screen",
            GLFW.GLFW_KEY_I,
            "key.categories.subservermod"
    ));

    public static final KeyMapping testKey = register(new KeyMapping(
            "key.subservermod.test_key",
            GLFW.GLFW_KEY_P,
            "key.categories.subservermod"
    ));

    public static KeyMapping register(KeyMapping keyMapping) {
        keyList.add(keyMapping);
        return keyMapping;
    }

    @Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class KeyRegisterHandler {
        @SubscribeEvent
        public static void onKeyRegisterEvent(RegisterKeyMappingsEvent event) {
            Subservermod.LOGGER.info("Registering Keybinds");
            keyList.forEach(event::register);
        }
    }
}
