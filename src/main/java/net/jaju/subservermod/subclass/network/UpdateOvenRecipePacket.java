package net.jaju.subservermod.subclass.network;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.sound.SoundPlayer;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenBlockEntity;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenContainer;
import net.jaju.subservermod.subclass.skill.miner.crafting.CraftingBlockEntity;
import net.jaju.subservermod.util.CommandExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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