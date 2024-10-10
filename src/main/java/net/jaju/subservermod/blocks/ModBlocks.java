package net.jaju.subservermod.blocks;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.items.ModItems;
import net.jaju.subservermod.blocks.subclass.alchemist.brewing.BrewingBlock;
import net.jaju.subservermod.blocks.subclass.alchemist.teleport.TeleportBlock;
import net.jaju.subservermod.blocks.subclass.chef.chefblock.ChefBlock;
import net.jaju.subservermod.blocks.subclass.farmer.large_oven.LargeOvenBlock;
import net.jaju.subservermod.blocks.subclass.farmer.middle_oven.MiddleOvenBlock;
import net.jaju.subservermod.blocks.subclass.farmer.scarecrow_block.ScareCrowBlock;
import net.jaju.subservermod.blocks.subclass.fisherman.fishing_rod.FishingRodBlock;
import net.jaju.subservermod.blocks.subclass.fisherman.raw_fished.CuttingBoardBlock;
import net.jaju.subservermod.blocks.subclass.miner.crafting.CraftingBlock;
import net.jaju.subservermod.blocks.subclass.farmer.oven.OvenBlock;
import net.jaju.subservermod.blocks.subclass.miner.upgrade_crafting.UpgradeCraftingBlock;
import net.jaju.subservermod.blocks.subclass.woodcutter.woodcuttingunion.WoodcuttingUnionBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Subservermod.MOD_ID);

    public static final RegistryObject<Block> SCARECROW_BLOCK = registerBlock("scarecrow_block",
            () -> new ScareCrowBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> OVEN_BLOCK = registerBlock("oven_block",
            () -> new OvenBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> MIDDLE_OVEN_BLOCK = registerBlock("middle_oven_block",
            () -> new MiddleOvenBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> LARGE_OVEN_BLOCK = registerBlock("large_oven_block",
            () -> new LargeOvenBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> CRAFTING_BLOCK = registerBlock("crafting_block",
            () -> new CraftingBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> UPGRADE_CRAFTING_BLOCK = registerBlock("upgrade_crafting_block",
            () -> new UpgradeCraftingBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> BREWING_BLOCK = registerBlock("brewing_block",
            () -> new BrewingBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> TELEPORT_BLOCK = registerBlock("teleport_block",
            () -> new TeleportBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> WOODCUTTINGUNION_BLOCK = registerBlock("woodcutting_union_block",
            () -> new WoodcuttingUnionBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> CHEF_BLOCK = registerBlock("chef_block",
            () -> new ChefBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> CUTTING_BOARD_BLOCK = registerBlock("cutting_board_block",
            () -> new CuttingBoardBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));
    public static final RegistryObject<Block> FISHING_ROD_BLOCK = registerBlock("fishing_rod_block",
            () -> new FishingRodBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND).sound(SoundType.METAL)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
