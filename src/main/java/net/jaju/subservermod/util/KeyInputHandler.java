package net.jaju.subservermod.util;

import net.jaju.subservermod.encyclopedia.EncyclopediaManager;
import net.jaju.subservermod.encyclopedia.screen.EncyclopediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

@Mod.EventBusSubscriber
public class KeyInputHandler {
    public static final EncyclopediaManager landManager = new EncyclopediaManager();

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null) {
            if (KeyBindings.openCustomScreenKey.consumeClick()) {
                assert Minecraft.getInstance().player != null;
                Minecraft.getInstance().setScreen(new EncyclopediaScreen(Minecraft.getInstance().player));
            }
        }
    }
}