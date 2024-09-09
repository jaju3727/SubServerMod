package net.jaju.subservermod.block;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.skill.alchemist.brewing.BrewingBlockEntity;
import net.jaju.subservermod.subclass.skill.alchemist.teleport.TeleportBlockEntity;
import net.jaju.subservermod.subclass.skill.chef.chefblock.ChefBlockEntity;
import net.jaju.subservermod.subclass.skill.farmer.large_oven.LargeOvenBlockEntity;
import net.jaju.subservermod.subclass.skill.farmer.middle_oven.MiddleOvenBlockEntity;
import net.jaju.subservermod.subclass.skill.farmer.scarecrow_block.ScareCrowBlockEntity;
import net.jaju.subservermod.subclass.skill.fisherman.fishing_rod.FishingRodBlockEntity;
import net.jaju.subservermod.subclass.skill.fisherman.raw_fished.CuttingBoardBlockEntity;
import net.jaju.subservermod.subclass.skill.miner.crafting.CraftingBlockEntity;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenBlockEntity;
import net.jaju.subservermod.subclass.skill.miner.upgrade_crafting.UpgradeCraftingBlockEntity;
import net.jaju.subservermod.subclass.skill.woodcutter.woodcuttingunion.WoodcuttingUnionBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Subservermod.MOD_ID);

    public static final RegistryObject<BlockEntityType<OvenBlockEntity>> OVEN_BLOCK_ENTITY = BLOCK_ENTITIES.register("oven_block_entity", () ->
            BlockEntityType.Builder.of(OvenBlockEntity::new, ModBlocks.OVEN_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<MiddleOvenBlockEntity>> MIDDLE_OVEN_BLOCK_ENTITY = BLOCK_ENTITIES.register("middle_oven_block_entity", () ->
            BlockEntityType.Builder.of(MiddleOvenBlockEntity::new, ModBlocks.MIDDLE_OVEN_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<LargeOvenBlockEntity>> LARGE_OVEN_BLOCK_ENTITY = BLOCK_ENTITIES.register("large_oven_block_entity", () ->
            BlockEntityType.Builder.of(LargeOvenBlockEntity::new, ModBlocks.LARGE_OVEN_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<CraftingBlockEntity>> CRAFTING_BLOCK_ENTITY = BLOCK_ENTITIES.register("crafting_block_entity", () ->
            BlockEntityType.Builder.of(CraftingBlockEntity::new, ModBlocks.CRAFTING_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<UpgradeCraftingBlockEntity>> UPGRADE_CRAFTING_BLOCK_ENTITY = BLOCK_ENTITIES.register("upgrade_crafting_block_entity", () ->
            BlockEntityType.Builder.of(UpgradeCraftingBlockEntity::new, ModBlocks.UPGRADE_CRAFTING_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<BrewingBlockEntity>> BREWING_BLOCK_ENTITY = BLOCK_ENTITIES.register("brewing_block_entity", () ->
            BlockEntityType.Builder.of(BrewingBlockEntity::new, ModBlocks.BREWING_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<TeleportBlockEntity>> TELEPORT_BLOCK_ENTITY = BLOCK_ENTITIES.register("teleport_block_entity", () ->
            BlockEntityType.Builder.of(TeleportBlockEntity::new, ModBlocks.TELEPORT_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<WoodcuttingUnionBlockEntity>> WOODCUTTINGUNION_BLOCK_ENTITY = BLOCK_ENTITIES.register("woodcutting_union_block_entity", () ->
            BlockEntityType.Builder.of(WoodcuttingUnionBlockEntity::new, ModBlocks.WOODCUTTINGUNION_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChefBlockEntity>> CHEF_BLOCK_ENTITY = BLOCK_ENTITIES.register("chef_block_entity", () ->
            BlockEntityType.Builder.of(ChefBlockEntity::new, ModBlocks.CHEF_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<CuttingBoardBlockEntity>> CUTTING_BOARD_BLOCK_ENTITY = BLOCK_ENTITIES.register("cutting_board_block_entity", () ->
            BlockEntityType.Builder.of(CuttingBoardBlockEntity::new, ModBlocks.CUTTING_BOARD_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FishingRodBlockEntity>> FISHING_ROD_BLOCK_ENTITY = BLOCK_ENTITIES.register("fishing_rod_block_entity", () ->
            BlockEntityType.Builder.of(FishingRodBlockEntity::new, ModBlocks.FISHING_ROD_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<ScareCrowBlockEntity>> SCARECROW_BLOCK_ENTITY = BLOCK_ENTITIES.register("scarecrow_block_entity", () ->
            BlockEntityType.Builder.of(ScareCrowBlockEntity::new, ModBlocks.SCARECROW_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
