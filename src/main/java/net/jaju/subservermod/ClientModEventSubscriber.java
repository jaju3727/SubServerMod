package net.jaju.subservermod;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.subclass.skill.chef.chefblock.ChefBlockRenderer;
import net.jaju.subservermod.subclass.skill.fisherman.fishing_rod.FishingRodBlockRenderer;
import net.jaju.subservermod.subclass.skill.fisherman.raw_fished.CuttingBoardBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEventSubscriber {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityRenderers.register(ModBlockEntities.CHEF_BLOCK_ENTITY.get(), ChefBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.CUTTING_BOARD_BLOCK_ENTITY.get(), CuttingBoardBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.FISHING_ROD_BLOCK_ENTITY.get(), FishingRodBlockRenderer::new);
    }
}