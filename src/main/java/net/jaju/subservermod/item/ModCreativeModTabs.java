package net.jaju.subservermod.item;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.block.ModBlocks;
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
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItem.CONSTRUNTING_ALLOW.get()))
                    .title(Component.translatable("섭 아이템"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItem.CONSTRUNTING_ALLOW.get());
                        pOutput.accept(ModItem.HOLY_GRAIL.get());
                        pOutput.accept(ModItem.INVENTORY_SAVE_ITEM.get());
                        pOutput.accept(ModItem.RANDOMBOX.get());
                        pOutput.accept(ModItem.BUTTER.get());
                        pOutput.accept(ModItem.CREAM_BREAD.get());
                        pOutput.accept(ModItem.WHIPPED_CREAM.get());
                        pOutput.accept(ModItem.CROISSANT.get());
                        pOutput.accept(ModItem.BAGUETTE.get());
                        pOutput.accept(ModItem.CHOCOLATE.get());
                        pOutput.accept(ModItem.BROWNIE.get());
                        pOutput.accept(ModItem.CRAFTING_TOOL.get());
                        pOutput.accept(ModItem.DIAMOND_CUBIC.get());
                        pOutput.accept(ModItem.EMERALD_CUBIC.get());
                        pOutput.accept(ModItem.GOLD_RING.get());
                        pOutput.accept(ModItem.LAPIS_LAZULI_MARBLE.get());
                        pOutput.accept(ModItem.REDSTONE_MARBLE.get());
                        pOutput.accept(ModItem.WOODEN_CHAIR.get());
                        pOutput.accept(ModItem.WOODEN_DRAWER.get());
                        pOutput.accept(ModItem.WOODEN_CUTTING_BOARD.get());
                        pOutput.accept(ModItem.WOODEN_SHELF.get());
                        pOutput.accept(ModItem.WOODEN_TABLE.get());
                        pOutput.accept(ModItem.APPLE_DIFFUSER.get());
                        pOutput.accept(ModItem.COOKING_OIL.get());
                        pOutput.accept(ModItem.BEEF_STEAK.get());
                        pOutput.accept(ModItem.LAMB_STEAK.get());
                        pOutput.accept(ModItem.ROAST_CHICKEN.get());
                        pOutput.accept(ModItem.PORK_BELLY.get());
                        pOutput.accept(ModItem.FRIED_EGG.get());
                        pOutput.accept(ModItem.COD_RAW_FISH.get());
                        pOutput.accept(ModItem.SALMON_RAW_FISH.get());
                        pOutput.accept(ModItem.SQUID.get());
                        pOutput.accept(ModItem.SQUID_SASHIMI.get());
                        pOutput.accept(ModItem.SASHIMI_KNIFE.get());
                        pOutput.accept(ModItem.SUB_COIN.get());
                        pOutput.accept(ModItem.CHEF_COIN.get());
                        pOutput.accept(ModItem.FISHERMAN_COIN.get());
                        pOutput.accept(ModItem.FARMER_COIN.get());
                        pOutput.accept(ModItem.ALCHEMIST_COIN.get());
                        pOutput.accept(ModItem.MINER_COIN.get());
                        pOutput.accept(ModItem.WOODCUTTER_COIN.get());
                        pOutput.accept(ModItem.FARMER_FORMER_1.get());
                        pOutput.accept(ModItem.FARMER_FORMER_2.get());
                        pOutput.accept(ModItem.FARMER_FORMER_3.get());
                        pOutput.accept(ModItem.CHEF_FORMER_1.get());
                        pOutput.accept(ModItem.CHEF_FORMER_2.get());
                        pOutput.accept(ModItem.CHEF_FORMER_3.get());
                        pOutput.accept(ModItem.FISHERMAN_FORMER_1.get());
                        pOutput.accept(ModItem.FISHERMAN_FORMER_2.get());
                        pOutput.accept(ModItem.FISHERMAN_FORMER_3.get());
                        pOutput.accept(ModItem.ALCHEMIST_FORMER_1.get());
                        pOutput.accept(ModItem.ALCHEMIST_FORMER_2.get());
                        pOutput.accept(ModItem.ALCHEMIST_FORMER_3.get());
                        pOutput.accept(ModItem.MINER_FORMER_1.get());
                        pOutput.accept(ModItem.MINER_FORMER_2.get());
                        pOutput.accept(ModItem.MINER_FORMER_3.get());
                        pOutput.accept(ModItem.WOODCUTTER_FORMER_1.get());
                        pOutput.accept(ModItem.WOODCUTTER_FORMER_2.get());
                        pOutput.accept(ModItem.WOODCUTTER_FORMER_3.get());
                        pOutput.accept(ModItem.CLASS_FORMER_1.get());
                        pOutput.accept(ModItem.CLASS_FORMER_2.get());
                        pOutput.accept(ModItem.CLASS_FORMER_3.get());
                        pOutput.accept(ModItem.LOW_CLASS_HEALING_POTION.get());
                        pOutput.accept(ModItem.MIDDLE_CLASS_HEALING_POTION.get());
                        pOutput.accept(ModItem.HIGH_CLASS_HEALING_POTION.get());
                        pOutput.accept(ModItem.TORY_BURGER.get());
                        pOutput.accept(ModItem.HOWSWEET.get());
                        pOutput.accept(ModItem.SPECIAL_STORE_TELEPORT.get());
                        pOutput.accept(ModItem.PERRYGRASS_TELEPORT.get());
                        pOutput.accept(ModItem.GEMSHORE_TELEPORT.get());
                        pOutput.accept(ModItem.RUNEGROVE_TELEPORT.get());

                        pOutput.accept(ModBlocks.SCARECROW_BLOCK.get());
                        pOutput.accept(ModBlocks.OVEN_BLOCK.get());
                        pOutput.accept(ModBlocks.MIDDLE_OVEN_BLOCK.get());
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
