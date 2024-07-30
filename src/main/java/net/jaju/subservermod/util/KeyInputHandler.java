package net.jaju.subservermod.util;

import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.auction.AuctionScreen;
import net.jaju.subservermod.encyclopedia.EncyclopediaManager;
import net.jaju.subservermod.encyclopedia.screen.EncyclopediaScreen;
import net.jaju.subservermod.landsystem.screen.LandManagerScreen;
import net.jaju.subservermod.mailbox.MailboxScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, value = Dist.CLIENT)
public class KeyInputHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null) {
            if (KeyBindings.openCustomScreenKey.consumeClick()) {
                assert Minecraft.getInstance().player != null;
                Minecraft.getInstance().setScreen(new LandManagerScreen(Component.empty(), Minecraft.getInstance().player));
//                Minecraft.getInstance().setScreen(new MailboxScreen(Minecraft.getInstance().player));
            } else if (KeyBindings.testKey.consumeClick()) {
                assert Minecraft.getInstance().player != null;
//                Minecraft.getInstance().setScreen(new AuctionScreen(Minecraft.getInstance().player));
                Minecraft.getInstance().setScreen(new EncyclopediaScreen(Minecraft.getInstance().player));
            }
        }
    }
}