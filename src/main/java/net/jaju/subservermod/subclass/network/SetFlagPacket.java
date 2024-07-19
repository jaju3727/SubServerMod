package net.jaju.subservermod.subclass.network;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.subclass.skill.miner.crafting.CraftingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

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