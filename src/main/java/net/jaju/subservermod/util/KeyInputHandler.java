package net.jaju.subservermod.util;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.guide.GuideScreen;
import net.jaju.subservermod.integrated_menu.IntegratedMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, value = Dist.CLIENT)
public class KeyInputHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null) {
            if (event.getKey() == GLFW.GLFW_KEY_GRAVE_ACCENT && event.getAction() == GLFW.GLFW_PRESS) {
                Player player = Minecraft.getInstance().player;
                assert player != null;
                Minecraft.getInstance().setScreen(new IntegratedMenuScreen(player));
            } else if (event.getKey() == GLFW.GLFW_KEY_F12 && event.getAction() == GLFW.GLFW_PRESS) {
                Player player = Minecraft.getInstance().player;
                assert player != null;
                Minecraft.getInstance().setScreen(new GuideScreen());
            }

        }
    }
}