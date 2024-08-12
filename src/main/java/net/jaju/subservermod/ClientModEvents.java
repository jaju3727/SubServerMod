package net.jaju.subservermod;

import net.jaju.subservermod.entity.ModEntities;
import net.jaju.subservermod.entity.model.PlayerEntityModel;
import net.jaju.subservermod.entity.model.ShopEntityModel;
import net.jaju.subservermod.entity.rederer.PlayerEntityRenderer;
import net.jaju.subservermod.entity.rederer.ShopEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHOP_ENTITY.get(), ShopEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.PLAYER_ENTITY.get(), PlayerEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ShopEntityModel.LAYER_LOCATION, ShopEntityModel::createBodyLayer);
        event.registerLayerDefinition(PlayerEntityModel.LAYER_LOCATION, PlayerEntityModel::createBodyLayer);
    }
}
