package net.jaju.subservermod;

import net.jaju.subservermod.coinsystem.network.CoinDataSyncPacket;
//import net.jaju.subservermod.encyclopedia.network.EncyclopediaDataPacket;
import net.jaju.subservermod.encyclopedia.network.EncyclopediaPacket;
import net.jaju.subservermod.encyclopedia.network.ItemDiscoveryPacket;
import net.jaju.subservermod.landsystem.network.packet.*;
import net.jaju.subservermod.shopsystem.network.ShopEntityDataPacket;
import net.jaju.subservermod.shopsystem.network.UpdateInventoryPacket;
import net.jaju.subservermod.subclass.network.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Subservermod.MOD_ID, "subservermod"),
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
        INSTANCE.registerMessage(id++, GaugeSendToEntityPacket.class, GaugeSendToEntityPacket::encode, GaugeSendToEntityPacket::decode, GaugeSendToEntityPacket::handle);
        INSTANCE.registerMessage(id++, GaugeSendToClientPacket.class, GaugeSendToClientPacket::encode, GaugeSendToClientPacket::decode, GaugeSendToClientPacket::handle);
        INSTANCE.registerMessage(id++, BrewingVarSendToClientPacket.class, BrewingVarSendToClientPacket::encode, BrewingVarSendToClientPacket::decode, BrewingVarSendToClientPacket::handle);

        INSTANCE.registerMessage(id++, ItemDiscoveryPacket.class, ItemDiscoveryPacket::encode, ItemDiscoveryPacket::decode, ItemDiscoveryPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, EncyclopediaPacket.class, EncyclopediaPacket::encode, EncyclopediaPacket::decode, EncyclopediaPacket::handle);

        INSTANCE.messageBuilder(CoinDataSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CoinDataSyncPacket::new)
                .encoder(CoinDataSyncPacket::toBytes)
                .consumerMainThread(CoinDataSyncPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetFlagPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetFlagPacket::decode)
                .encoder(SetFlagPacket::encode)
                .consumerMainThread(SetFlagPacket::handle)
                .add();
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
