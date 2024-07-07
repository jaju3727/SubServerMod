package net.jaju.subservermod;

import net.jaju.subservermod.shopsystem.entity.ModEntities;
import net.jaju.subservermod.shopsystem.entity.model.ShopEntityModel;
import net.jaju.subservermod.shopsystem.entity.rederer.ShopEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CUSTOM_ENTITY.get(), ShopEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ShopEntityModel.LAYER_LOCATION, ShopEntityModel::createBodyLayer);
    }
}
