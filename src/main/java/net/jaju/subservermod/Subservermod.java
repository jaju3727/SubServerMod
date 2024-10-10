package net.jaju.subservermod;

import com.mojang.logging.LogUtils;
import net.jaju.subservermod.blocks.ModBlockEntities;
import net.jaju.subservermod.blocks.ModBlocks;
import net.jaju.subservermod.manager.ClassManager;
import net.jaju.subservermod.manager.CoinManager;
import net.jaju.subservermod.items.ModCreativeModTabs;
import net.jaju.subservermod.items.ModItems;
import net.jaju.subservermod.events.ChunkOwnershipHandler;
import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.network.landsystem.ServerSideEventHandler;
import net.jaju.subservermod.commands.mailbox.MailboxCommand;
import net.jaju.subservermod.manager.MailboxManager;
import net.jaju.subservermod.entity.ModEntities;
import net.jaju.subservermod.sound.ModSounds;
import net.jaju.subservermod.util.KeyInputHandler;
import net.jaju.subservermod.util.ModItemModelProvider;
import net.jaju.subservermod.util.ModItemTagGenerator;
import net.jaju.subservermod.events.VillageEventHandlers;
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
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModContainers.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(KeyInputHandler.class);
        MinecraftForge.EVENT_BUS.register(new ChunkOwnershipHandler());
        MinecraftForge.EVENT_BUS.register(new ServerSideEventHandler());
        MinecraftForge.EVENT_BUS.register(MailboxManager.class);
        MinecraftForge.EVENT_BUS.register(MailboxCommand.class);
        MinecraftForge.EVENT_BUS.register(CoinManager.class);
        MinecraftForge.EVENT_BUS.register(VillageEventHandlers.class);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
            MinecraftForge.EVENT_BUS.register(ClientModEventSubscriber.class);
        });

        modEventBus.addListener(this::gatherData);

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
        ClassManager.loadClassData();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ModScreens.register();
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BREWING_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.OVEN_BLOCK.get(), RenderType.cutout());
        });

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Creative tab에 아이템 추가 로직
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        ClassManager.loadClassData();
        CoinManager.loadCoinData();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ClassManager.saveClassData();
        CoinManager.saveCoinData();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // 클라이언트 전용 설정
        }
    }
}
