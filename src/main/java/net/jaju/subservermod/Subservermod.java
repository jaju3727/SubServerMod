package net.jaju.subservermod;

import com.mojang.logging.LogUtils;
import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.block.ModBlocks;
import net.jaju.subservermod.coinsystem.CoinHud;
import net.jaju.subservermod.item.ModCreativeModTabs;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.landsystem.ChunkOwnershipHandler;
import net.jaju.subservermod.landsystem.network.ServerSideEventHandler;
import net.jaju.subservermod.mailbox.MailboxCommand;
import net.jaju.subservermod.mailbox.MailboxManager;
import net.jaju.subservermod.entity.ModEntities;
import net.jaju.subservermod.sound.ModSounds;
import net.jaju.subservermod.subclass.ClassManagement;
import net.jaju.subservermod.util.KeyInputHandler;
import net.jaju.subservermod.util.ModItemModelProvider;
import net.jaju.subservermod.util.ModItemTagGenerator;
import net.jaju.subservermod.village.VillageEventHandlers;
import net.jaju.subservermod.village.VillageHudRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(Subservermod.MOD_ID)
public class Subservermod {
    public static final String MOD_ID = "subservermod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Subservermod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModSounds.register(modEventBus);
        ModCreativeModTabs.register(modEventBus);
        ModItem.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModContainers.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(KeyInputHandler.class);
        MinecraftForge.EVENT_BUS.register(new ChunkOwnershipHandler());
        LOGGER.info("10");
        MinecraftForge.EVENT_BUS.register(new ServerSideEventHandler());
        LOGGER.info("9");
        MinecraftForge.EVENT_BUS.register(MailboxManager.class);
        LOGGER.info("8");
        MinecraftForge.EVENT_BUS.register(MailboxCommand.class);
        LOGGER.info("7");
        MinecraftForge.EVENT_BUS.register(CoinHud.class);
        LOGGER.info("6");
        MinecraftForge.EVENT_BUS.register(VillageEventHandlers.class);
        LOGGER.info("5");
        modEventBus.addListener(this::commonSetup);
        LOGGER.info("4");
        modEventBus.addListener(this::addCreative);
        LOGGER.info("3");
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
            MinecraftForge.EVENT_BUS.register(ClientModEventSubscriber.class);
        });
        LOGGER.info("2");
        modEventBus.addListener(this::gatherData);
        LOGGER.info("1");

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CompletableFuture<HolderLookup.Provider> futureProvider = CompletableFuture.supplyAsync(() -> {
            // Implement your logic to supply a HolderLookup.Provider
            return null;
        });

        CompletableFuture<TagsProvider.TagLookup<Block>> futureTagLookup = CompletableFuture.supplyAsync(() -> {
            // Implement your logic to supply a TagLookup<Block>
            return null;
        });

        generator.addProvider(event.includeServer(), new ModItemTagGenerator(generator.getPackOutput(), futureProvider, futureTagLookup, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator.getPackOutput(), existingFileHelper));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetworking.registerMessages();
        ClassManagement.loadClassData();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ModScreens.register();
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BREWING_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.OVEN_BLOCK.get(), RenderType.cutout());
        });

        MinecraftForge.EVENT_BUS.register(VillageHudRenderer.class);

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Creative tab에 아이템 추가 로직
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        ClassManagement.loadClassData();
        CoinHud.loadCoinData();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ClassManagement.saveClassData();
        CoinHud.saveCoinData();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // 클라이언트 전용 설정
        }
    }
}
