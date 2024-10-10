package net.jaju.subservermod.items;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.blocks.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Subservermod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS.register("tutorial_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CONSTRUNTING_ALLOW.get()))
                    .title(Component.translatable("섭 아이템"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.CONSTRUNTING_ALLOW.get());
                        pOutput.accept(ModItems.HOLY_GRAIL.get());
                        pOutput.accept(ModItems.INVENTORY_SAVE_ITEM.get());
                        pOutput.accept(ModItems.RANDOMBOX.get());
                        pOutput.accept(ModItems.BUTTER.get());
                        pOutput.accept(ModItems.CREAM_BREAD.get());
                        pOutput.accept(ModItems.WHIPPED_CREAM.get());
                        pOutput.accept(ModItems.CROISSANT.get());
                        pOutput.accept(ModItems.BAGUETTE.get());
                        pOutput.accept(ModItems.CHOCOLATE.get());
                        pOutput.accept(ModItems.BROWNIE.get());
                        pOutput.accept(ModItems.CRAFTING_TOOL.get());
                        pOutput.accept(ModItems.DIAMOND_CUBIC.get());
                        pOutput.accept(ModItems.EMERALD_CUBIC.get());
                        pOutput.accept(ModItems.GOLD_RING.get());
                        pOutput.accept(ModItems.LAPIS_LAZULI_MARBLE.get());
                        pOutput.accept(ModItems.REDSTONE_MARBLE.get());
                        pOutput.accept(ModItems.WOODEN_CHAIR.get());
                        pOutput.accept(ModItems.WOODEN_DRAWER.get());
                        pOutput.accept(ModItems.WOODEN_CUTTING_BOARD.get());
                        pOutput.accept(ModItems.WOODEN_SHELF.get());
                        pOutput.accept(ModItems.WOODEN_TABLE.get());
                        pOutput.accept(ModItems.APPLE_DIFFUSER.get());
                        pOutput.accept(ModItems.COOKING_OIL.get());
                        pOutput.accept(ModItems.BEEF_STEAK.get());
                        pOutput.accept(ModItems.LAMB_STEAK.get());
                        pOutput.accept(ModItems.ROAST_CHICKEN.get());
                        pOutput.accept(ModItems.PORK_BELLY.get());
                        pOutput.accept(ModItems.FRIED_EGG.get());
                        pOutput.accept(ModItems.COD_RAW_FISH.get());
                        pOutput.accept(ModItems.SALMON_RAW_FISH.get());
                        pOutput.accept(ModItems.SQUID.get());
                        pOutput.accept(ModItems.SQUID_SASHIMI.get());
                        pOutput.accept(ModItems.SASHIMI_KNIFE.get());
                        pOutput.accept(ModItems.SUB_COIN.get());
                        pOutput.accept(ModItems.CHEF_COIN.get());
                        pOutput.accept(ModItems.FISHERMAN_COIN.get());
                        pOutput.accept(ModItems.FARMER_COIN.get());
                        pOutput.accept(ModItems.ALCHEMIST_COIN.get());
                        pOutput.accept(ModItems.MINER_COIN.get());
                        pOutput.accept(ModItems.WOODCUTTER_COIN.get());
                        pOutput.accept(ModItems.FARMER_FORMER_1.get());
                        pOutput.accept(ModItems.FARMER_FORMER_2.get());
                        pOutput.accept(ModItems.FARMER_FORMER_3.get());
                        pOutput.accept(ModItems.CHEF_FORMER_1.get());
                        pOutput.accept(ModItems.CHEF_FORMER_2.get());
                        pOutput.accept(ModItems.CHEF_FORMER_3.get());
                        pOutput.accept(ModItems.FISHERMAN_FORMER_1.get());
                        pOutput.accept(ModItems.FISHERMAN_FORMER_2.get());
                        pOutput.accept(ModItems.FISHERMAN_FORMER_3.get());
                        pOutput.accept(ModItems.ALCHEMIST_FORMER_1.get());
                        pOutput.accept(ModItems.ALCHEMIST_FORMER_2.get());
                        pOutput.accept(ModItems.ALCHEMIST_FORMER_3.get());
                        pOutput.accept(ModItems.MINER_FORMER_1.get());
                        pOutput.accept(ModItems.MINER_FORMER_2.get());
                        pOutput.accept(ModItems.MINER_FORMER_3.get());
                        pOutput.accept(ModItems.WOODCUTTER_FORMER_1.get());
                        pOutput.accept(ModItems.WOODCUTTER_FORMER_2.get());
                        pOutput.accept(ModItems.WOODCUTTER_FORMER_3.get());
                        pOutput.accept(ModItems.CLASS_FORMER_1.get());
                        pOutput.accept(ModItems.CLASS_FORMER_2.get());
                        pOutput.accept(ModItems.CLASS_FORMER_3.get());
                        pOutput.accept(ModItems.LOW_CLASS_HEALING_POTION.get());
                        pOutput.accept(ModItems.MIDDLE_CLASS_HEALING_POTION.get());
                        pOutput.accept(ModItems.HIGH_CLASS_HEALING_POTION.get());
                        pOutput.accept(ModItems.TORY_BURGER.get());
                        pOutput.accept(ModItems.HOWSWEET.get());
                        pOutput.accept(ModItems.SPECIAL_STORE_TELEPORT.get());
                        pOutput.accept(ModItems.PERRYGRASS_TELEPORT.get());
                        pOutput.accept(ModItems.GEMSHORE_TELEPORT.get());
                        pOutput.accept(ModItems.RUNEGROVE_TELEPORT.get());
                        pOutput.accept(ModItems.STAT_RESET.get());
                        pOutput.accept(ModItems.STAT_POINT.get());
                        pOutput.accept(ModItems.CLASS_TRADE.get());

                        pOutput.accept(ModBlocks.SCARECROW_BLOCK.get());
                        pOutput.accept(ModBlocks.OVEN_BLOCK.get());
                        pOutput.accept(ModBlocks.MIDDLE_OVEN_BLOCK.get());
                        pOutput.accept(ModBlocks.LARGE_OVEN_BLOCK.get());
                        pOutput.accept(ModBlocks.CRAFTING_BLOCK.get());
                        pOutput.accept(ModBlocks.BREWING_BLOCK.get());
                        pOutput.accept(ModBlocks.TELEPORT_BLOCK.get());
                        pOutput.accept(ModBlocks.WOODCUTTINGUNION_BLOCK.get());
                        pOutput.accept(ModBlocks.CHEF_BLOCK.get());
                        pOutput.accept(ModBlocks.CUTTING_BOARD_BLOCK.get());
                        pOutput.accept(ModBlocks.FISHING_ROD_BLOCK.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
