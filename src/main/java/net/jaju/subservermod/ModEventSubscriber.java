package net.jaju.subservermod;

import com.mojang.brigadier.CommandDispatcher;
import net.jaju.subservermod.commands.coinsystem.CoinCommands;
import net.jaju.subservermod.entity.ModEntities;
import net.jaju.subservermod.entity.rederer.PlayerEntityRenderer;
import net.jaju.subservermod.commands.player.PlayerCommand;
import net.jaju.subservermod.entity.rederer.ShopEntityRenderer;
import net.jaju.subservermod.commands.shopsystem.ShopCommands;
import net.jaju.subservermod.commands.subclass.ClassCommand;
import net.jaju.subservermod.commands.village.VillageCommand;
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
        PlayerCommand.register(dispatcher, buildContext);
        CoinCommands.onRegisterCommands(event);
        ClassCommand.register(dispatcher);
        VillageCommand.register(dispatcher);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHOP_ENTITY.get(), ShopEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.PLAYER_ENTITY.get(), PlayerEntityRenderer::new);
    }
}
