package net.jaju.subservermod.item;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Subservermod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS.register("tutorial_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItem.CONSTRUNTING_ALLOW.get()))
                    .title(Component.translatable("creativetab.tutorial_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItem.CONSTRUNTING_ALLOW.get());
                        pOutput.accept(ModItem.BUTTER.get());
                        pOutput.accept(ModItem.CREAM_BREAD.get());
                        pOutput.accept(ModItem.WHIPPED_CREAM.get());
                        pOutput.accept(ModItem.CROISSANT.get());
                        pOutput.accept(ModItem.CRAFTING_TOOL.get());
                        pOutput.accept(ModItem.DIAMOND_CUBIC.get());
                        pOutput.accept(ModItem.EMERALD_CUBIC.get());
                        pOutput.accept(ModItem.GOLD_RING.get());
                        pOutput.accept(ModItem.LAPIS_LAZULI_MARBLE.get());
                        pOutput.accept(ModItem.REDSTONE_MARBLE.get());

                        pOutput.accept(ModBlocks.SCARECROW_BLOCK.get());
                        pOutput.accept(ModBlocks.OVEN_BLOCK.get());
                        pOutput.accept(ModBlocks.CRAFTING_BLOCK.get());
                        pOutput.accept(ModBlocks.BREWING_BLOCK.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
