package net.jaju.subservermod.shopsystem.entity;

import net.jaju.subservermod.Subservermod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Subservermod.MOD_ID);

    public static final RegistryObject<EntityType<ShopEntity>> CUSTOM_ENTITY = ENTITIES.register("custom_entity",
            () -> EntityType.Builder.of(ShopEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(Subservermod.MOD_ID, "custom_entity").toString()));

    @Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EntityAttributesRegisterHandler {
        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(CUSTOM_ENTITY.get(), ShopEntity.createAttributes().build());
        }
    }
}
