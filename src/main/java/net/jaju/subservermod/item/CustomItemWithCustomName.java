package net.jaju.subservermod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomItemWithCustomName extends Item {
    private final String customName;
    private final String lore;

    public CustomItemWithCustomName(Properties properties, String customName, String lore) {
        super(properties);
        this.customName = customName;
        this.lore = lore;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(this.customName);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(lore));
    }
}