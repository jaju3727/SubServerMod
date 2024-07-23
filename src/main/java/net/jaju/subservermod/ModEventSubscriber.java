package net.jaju.subservermod;

import com.mojang.brigadier.CommandDispatcher;
import net.jaju.subservermod.coinsystem.CoinCommands;
import net.jaju.subservermod.shopsystem.entity.ModEntities;
import net.jaju.subservermod.shopsystem.ShopCommands;
import net.jaju.subservermod.shopsystem.entity.model.ShopEntityModel;
import net.jaju.subservermod.shopsystem.entity.rederer.ShopEntityRenderer;
import net.jaju.subservermod.subclass.ClassCommand;
import net.jaju.subservermod.subclass.Woodcutter;
import net.jaju.subservermod.subclass.skill.woodcutter.woodcutterinventory.WoodcutterManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class ModEventSubscriber {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();
        ShopCommands.register(dispatcher, buildContext);
        CoinCommands.onRegisterCommands(event);
        ClassCommand.register(dispatcher);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CUSTOM_ENTITY.get(), ShopEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onPlayerSave(PlayerEvent.SaveToFile event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            WoodcutterManager.saveWoodcutterData(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoad(PlayerEvent.LoadFromFile event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            WoodcutterManager.loadWoodcutterData(player);
        }
    }
}
