package net.jaju.subservermod.network.subclass;

import net.jaju.subservermod.blocks.ModBlockEntities;
import net.jaju.subservermod.blocks.subclass.miner.crafting.CraftingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetFlagPacket {
    private final BlockPos pos;
    private final boolean flag;

    public SetFlagPacket(BlockPos pos, boolean flag) {
        this.pos = pos;
        this.flag = flag;
    }

    public static void encode(SetFlagPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeBoolean(packet.flag);
    }

    public static SetFlagPacket decode(FriendlyByteBuf buffer) {
        return new SetFlagPacket(buffer.readBlockPos(), buffer.readBoolean());
    }

    public static void handle(SetFlagPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            context.getSender().level().getBlockEntity(packet.pos, ModBlockEntities.CRAFTING_BLOCK_ENTITY.get()).ifPresent(blockEntity -> {
                if (blockEntity instanceof CraftingBlockEntity) {
                    ((CraftingBlockEntity) blockEntity).setFlag(packet.flag);
                }
            });
        });
        context.setPacketHandled(true);
    }
}