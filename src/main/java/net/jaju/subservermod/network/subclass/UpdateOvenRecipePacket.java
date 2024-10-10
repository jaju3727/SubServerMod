package net.jaju.subservermod.network.subclass;

import net.jaju.subservermod.blocks.ModBlockEntities;
import net.jaju.subservermod.blocks.subclass.farmer.oven.OvenBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateOvenRecipePacket {
    private final BlockPos pos;
    private final String recipeName;

    public UpdateOvenRecipePacket(BlockPos pos, String recipeName) {
        this.pos = pos;
        this.recipeName = recipeName;
    }

    public static void encode(UpdateOvenRecipePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeUtf(packet.recipeName);
    }

    public static UpdateOvenRecipePacket decode(FriendlyByteBuf buffer) {
        return new UpdateOvenRecipePacket(buffer.readBlockPos(), buffer.readUtf());
    }

    public static void handle(UpdateOvenRecipePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer senderPlayer = context.getSender();
            senderPlayer.level().getBlockEntity(packet.pos, ModBlockEntities.OVEN_BLOCK_ENTITY.get()).ifPresent(blockEntity -> {
                if (blockEntity instanceof OvenBlockEntity) {
                    ((OvenBlockEntity) blockEntity).updateRecipeName(packet.recipeName, senderPlayer);
                }
            });
        });
        context.setPacketHandled(true);
    }
}