package net.jaju.subservermod.util;

import net.jaju.subservermod.landsystem.screen.LandManagerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "subservermod", value = Dist.CLIENT)
public class KeyInputHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null) {
            if (event.getKey() == GLFW.GLFW_KEY_I && event.getAction() == GLFW.GLFW_PRESS) {
                assert Minecraft.getInstance().player != null;
                Minecraft.getInstance().setScreen(new LandManagerScreen(Component.literal("Custom Screen"), Minecraft.getInstance().player));
            }
        }
    }
}