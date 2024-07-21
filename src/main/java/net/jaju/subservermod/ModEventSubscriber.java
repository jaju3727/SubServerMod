package net.jaju.subservermod;

import com.mojang.brigadier.CommandDispatcher;
import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.coinsystem.CoinCommands;
import net.jaju.subservermod.shopsystem.entity.ModEntities;
import net.jaju.subservermod.shopsystem.ShopCommands;
import net.jaju.subservermod.shopsystem.entity.model.ShopEntityModel;
import net.jaju.subservermod.shopsystem.entity.rederer.ShopEntityRenderer;
import net.jaju.subservermod.subclass.ClassCommand;
import net.jaju.subservermod.subclass.skill.chef.chefblock.ChefBlockRenderer;
import net.jaju.subservermod.subclass.skill.woodcutter.woodcutterinventory.WoodcutterInventoryCapability;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
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
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ShopEntityModel.LAYER_LOCATION, ShopEntityModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Player> event) {
        event.addCapability(new ResourceLocation(Subservermod.MOD_ID, "woodcutter_inventory"), new WoodcutterInventoryCapability());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(WoodcutterInventoryCapability.WOODCUTTER_INVENTORY_CAPABILITY).ifPresent(oldCap -> {
                event.getEntity().getCapability(WoodcutterInventoryCapability.WOODCUTTER_INVENTORY_CAPABILITY).ifPresent(newCap -> {
                    newCap.deserializeNBT(oldCap.serializeNBT());
                });
            });
        }
    }
}
