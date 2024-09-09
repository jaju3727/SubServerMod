package net.jaju.subservermod.subclass.network;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.subclass.skill.farmer.large_oven.LargeOvenBlockEntity;
import net.jaju.subservermod.subclass.skill.farmer.middle_oven.MiddleOvenBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateLargeOvenRecipePacket {
    private final BlockPos pos;
    private final String recipeName;

    public UpdateLargeOvenRecipePacket(BlockPos pos, String recipeName) {
        this.pos = pos;
        this.recipeName = recipeName;
    }

    public static void encode(UpdateLargeOvenRecipePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeUtf(packet.recipeName);
    }

    public static UpdateLargeOvenRecipePacket decode(FriendlyByteBuf buffer) {
        return new UpdateLargeOvenRecipePacket(buffer.readBlockPos(), buffer.readUtf());
    }

    public static void handle(UpdateLargeOvenRecipePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer senderPlayer = context.getSender();
            senderPlayer.level().getBlockEntity(packet.pos, ModBlockEntities.LARGE_OVEN_BLOCK_ENTITY.get()).ifPresent(blockEntity -> {
                if (blockEntity instanceof LargeOvenBlockEntity) {
                    (blockEntity).updateRecipeName(packet.recipeName, senderPlayer);
                }
            });
        });
        context.setPacketHandled(true);
    }
}