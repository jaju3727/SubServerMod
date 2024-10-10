package net.jaju.subservermod.items;

import net.jaju.subservermod.network.ModNetworking;
import net.jaju.subservermod.network.integrated_menu.TemporaryOpRequestPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CommandItem extends Item {
    private final String command;
    private static boolean isHandled = false;

    public CommandItem(Properties properties, String command) {
        super(properties);
        this.command = command;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            if (player != null && !command.isEmpty()) {
                if (isHandled) {
                    isHandled = false;
                    return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
                }
                isHandled = true;
                ModNetworking.INSTANCE.sendToServer(new TemporaryOpRequestPacket(command));

                ItemStack stack = player.getItemInHand(hand);
                if (!player.isCreative()) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.getInventory().removeItem(stack);
                    }
                }
            }
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }
}
