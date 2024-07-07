package net.jaju.subservermod;

import com.mojang.brigadier.CommandDispatcher;
import net.jaju.subservermod.shopsystem.entity.ModEntities;
import net.jaju.subservermod.shopsystem.ShopCommands;
import net.jaju.subservermod.shopsystem.entity.model.ShopEntityModel;
import net.jaju.subservermod.shopsystem.entity.rederer.ShopEntityRenderer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class ModEventSubscriber {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();
        ShopCommands.register(dispatcher, buildContext);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CUSTOM_ENTITY.get(), ShopEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ShopEntityModel.LAYER_LOCATION, ShopEntityModel::createBodyLayer);
    }
}
