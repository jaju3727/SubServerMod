package net.jaju.subservermod;

import com.mojang.brigadier.CommandDispatcher;
import net.jaju.subservermod.coinsystem.CoinCommands;
import net.jaju.subservermod.shopsystem.entity.ModEntities;
import net.jaju.subservermod.shopsystem.ShopCommands;
import net.jaju.subservermod.shopsystem.entity.rederer.ShopEntityRenderer;
import net.jaju.subservermod.subclass.ClassCommand;
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
        Subservermod.LOGGER.info("2");
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
}
