package net.jaju.subservermod.block;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.subclass.skill.alchemist.brewing.BrewingBlock;
import net.jaju.subservermod.subclass.skill.miner.crafting.CraftingBlock;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenBlock;
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
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.AMETHYST)));
    public static final RegistryObject<Block> OVEN_BLOCK = registerBlock("oven_block",
            () -> new OvenBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));
    public static final RegistryObject<Block> CRAFTING_BLOCK = registerBlock("crafting_block",
            () -> new CraftingBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));
    public static final RegistryObject<Block> BREWING_BLOCK = registerBlock("brewing_block",
            () -> new BrewingBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItem.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
