package net.jaju.subservermod;

import net.jaju.subservermod.landsystem.network.packet.*;
import net.jaju.subservermod.shopsystem.network.ShopEntityDataPacket;
import net.jaju.subservermod.shopsystem.network.UpdateInventoryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Subservermod.MOD_ID, "chunk_owners"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMessages() {
        int id = 0;
        INSTANCE.registerMessage(id++, ChunkOwnersPacket.class, ChunkOwnersPacket::encode, ChunkOwnersPacket::decode, ChunkOwnersPacket::handle);
        INSTANCE.registerMessage(id++, ChunkSharersPacket.class, ChunkSharersPacket::encode, ChunkSharersPacket::decode, ChunkSharersPacket::handle);
        INSTANCE.registerMessage(id++, ChunkOwnerUpdatePacket.class, ChunkOwnerUpdatePacket::encode, ChunkOwnerUpdatePacket::decode, ChunkOwnerUpdatePacket::handle);
        INSTANCE.registerMessage(id++, LandManagerMethodPacket.class, LandManagerMethodPacket::toBytes, LandManagerMethodPacket::new, LandManagerMethodPacket::handle);
        INSTANCE.registerMessage(id++, PlayerNamePacket.class, PlayerNamePacket::toBytes, PlayerNamePacket::new, PlayerNamePacket::handle);
        INSTANCE.registerMessage(id++, PlayerNameResponsePacket.class, PlayerNameResponsePacket::toBytes, PlayerNameResponsePacket::new, PlayerNameResponsePacket::handle);
        INSTANCE.registerMessage(id++, ShopEntityDataPacket.class, ShopEntityDataPacket::toBytes, ShopEntityDataPacket::new, ShopEntityDataPacket::handle);
        INSTANCE.registerMessage(id++, UpdateInventoryPacket.class, UpdateInventoryPacket::toBytes, UpdateInventoryPacket::new, UpdateInventoryPacket::handle);
    }
}
