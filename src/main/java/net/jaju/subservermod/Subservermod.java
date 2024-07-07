package net.jaju.subservermod;

import com.mojang.logging.LogUtils;
import net.jaju.subservermod.item.ModCreativeModTabs;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.landsystem.ChunkOwnershipHandler;
import net.jaju.subservermod.landsystem.network.ServerSideEventHandler;
import net.jaju.subservermod.shopsystem.entity.ModEntities;
import net.jaju.subservermod.shopsystem.screen.ShopContainer;
import net.jaju.subservermod.shopsystem.screen.ShopScreen;
import net.jaju.subservermod.util.KeyBindings;
import net.jaju.subservermod.util.KeyInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Subservermod.MOD_ID)
public class Subservermod {
    public static final String MOD_ID = "subservermod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Subservermod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModTabs.register(modEventBus);
        ModItem.register(modEventBus);
        ModContainers.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(KeyInputHandler.class);
        MinecraftForge.EVENT_BUS.register(new ChunkOwnershipHandler());
        MinecraftForge.EVENT_BUS.register(new ServerSideEventHandler());
        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::commonSetup);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::clientSetup));
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetworking.registerMessages();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ModScreens.register();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItem.CONSTRUNTING_ALLOW);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 서버 시작 시 로직
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // 클라이언트 전용 설정
        }
    }
}
