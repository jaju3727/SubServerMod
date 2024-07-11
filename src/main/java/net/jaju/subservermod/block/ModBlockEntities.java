package net.jaju.subservermod.block;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.skill.alchemist.brewing.BrewingBlockEntity;
import net.jaju.subservermod.subclass.skill.miner.crafting.CraftingBlockEntity;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Subservermod.MOD_ID);

    public static final RegistryObject<BlockEntityType<OvenBlockEntity>> OVEN_BLOCK_ENTITY = BLOCK_ENTITIES.register("oven_block_entity", () ->
            BlockEntityType.Builder.of(OvenBlockEntity::new, ModBlocks.OVEN_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<CraftingBlockEntity>> CRAFTING_BLOCK_ENTITY = BLOCK_ENTITIES.register("crafting_block_entity", () ->
            BlockEntityType.Builder.of(CraftingBlockEntity::new, ModBlocks.CRAFTING_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<BrewingBlockEntity>> BREWING_BLOCK_ENTITY = BLOCK_ENTITIES.register("brewing_block_entity", () ->
            BlockEntityType.Builder.of(BrewingBlockEntity::new, ModBlocks.BREWING_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
