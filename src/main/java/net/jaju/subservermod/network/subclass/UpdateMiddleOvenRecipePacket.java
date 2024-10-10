package net.jaju.subservermod.network.subclass;

import net.jaju.subservermod.blocks.ModBlockEntities;
import net.jaju.subservermod.blocks.subclass.farmer.middle_oven.MiddleOvenBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateMiddleOvenRecipePacket {
    private final BlockPos pos;
    private final String recipeName;

    public UpdateMiddleOvenRecipePacket(BlockPos pos, String recipeName) {
        this.pos = pos;
        this.recipeName = recipeName;
    }

    public static void encode(UpdateMiddleOvenRecipePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeUtf(packet.recipeName);
    }

    public static UpdateMiddleOvenRecipePacket decode(FriendlyByteBuf buffer) {
        return new UpdateMiddleOvenRecipePacket(buffer.readBlockPos(), buffer.readUtf());
    }

    public static void handle(UpdateMiddleOvenRecipePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer senderPlayer = context.getSender();
            senderPlayer.level().getBlockEntity(packet.pos, ModBlockEntities.MIDDLE_OVEN_BLOCK_ENTITY.get()).ifPresent(blockEntity -> {
                if (blockEntity instanceof MiddleOvenBlockEntity) {
                    ((MiddleOvenBlockEntity) blockEntity).updateRecipeName(packet.recipeName, senderPlayer);
                }
            });
        });
        context.setPacketHandled(true);
    }
}