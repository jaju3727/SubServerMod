package net.jaju.subservermod.item;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.sound.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;

public class TeleportItem extends Item {

    private final ResourceKey<Level> targetDimension;
    private final BlockPos targetPosition;

    public TeleportItem(Properties properties, ResourceKey<Level> targetDimension, BlockPos targetPosition) {
        super(properties);
        this.targetDimension = targetDimension;
        this.targetPosition = targetPosition;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ServerLevel targetLevel = serverPlayer.server.getLevel(targetDimension);

            if (targetLevel != null) {
                serverPlayer.teleportTo(targetLevel, targetPosition.getX() + 0.5, targetPosition.getY(), targetPosition.getZ() + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
                player.displayClientMessage(Component.literal("Teleported to " + targetPosition.getX() + ", " + targetPosition.getY() + ", " + targetPosition.getZ() + " in " + targetDimension.location()), true);
                itemStack.shrink(1);
//                SoundPlayer.playCustomSound(player, new ResourceLocation(Subservermod.MOD_ID, "gohome_sound"), 8.0f, 8.0f);
                return InteractionResultHolder.success(itemStack);
            } else {
                player.displayClientMessage(Component.literal("Failed to find target dimension: " + targetDimension.location()), true);
            }
        }
        return InteractionResultHolder.fail(itemStack);
    }
}
