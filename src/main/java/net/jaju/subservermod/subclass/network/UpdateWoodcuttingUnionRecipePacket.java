package net.jaju.subservermod.subclass.network;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenBlockEntity;
import net.jaju.subservermod.subclass.skill.woodcutter.woodcuttingunion.WoodcuttingUnionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateWoodcuttingUnionRecipePacket {
    private final BlockPos pos;
    private final ItemStack itemStack;

    public UpdateWoodcuttingUnionRecipePacket(BlockPos pos, ItemStack itemStack) {
        this.pos = pos;
        this.itemStack = itemStack;
    }

    public static void encode(UpdateWoodcuttingUnionRecipePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeItemStack(packet.itemStack, true);
    }

    public static UpdateWoodcuttingUnionRecipePacket decode(FriendlyByteBuf buffer) {
        return new UpdateWoodcuttingUnionRecipePacket(buffer.readBlockPos(), buffer.readItem());
    }

    public static void handle(UpdateWoodcuttingUnionRecipePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer senderPlayer = context.getSender();
            senderPlayer.level().getBlockEntity(packet.pos, ModBlockEntities.WOODCUTTINGUNION_BLOCK_ENTITY.get()).ifPresent(blockEntity -> {
                if (blockEntity instanceof WoodcuttingUnionBlockEntity) {
                    ((WoodcuttingUnionBlockEntity) blockEntity).updateRecipeName(packet.itemStack, senderPlayer);
                }
            });
        });
        context.setPacketHandled(true);
    }
}