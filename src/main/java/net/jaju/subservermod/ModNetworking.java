package net.jaju.subservermod;

import net.jaju.subservermod.auction.network.packet.AuctionPacket;
import net.jaju.subservermod.auction.network.packet.UpdateAuctionPacket;
import net.jaju.subservermod.coinsystem.network.*;
//import net.jaju.subservermod.encyclopedia.network.EncyclopediaDataPacket;
import net.jaju.subservermod.encyclopedia.network.EncyclopediaPacket;
import net.jaju.subservermod.encyclopedia.network.ItemDiscoveryPacket;
import net.jaju.subservermod.encyclopedia.network.giftGetPacket;
import net.jaju.subservermod.entity.packet.PlayerEntityPositionPacket;
import net.jaju.subservermod.entity.packet.ShopEntityPositionPacket;
import net.jaju.subservermod.integrated_menu.network.*;
import net.jaju.subservermod.landsystem.network.packet.*;
import net.jaju.subservermod.mailbox.network.AddItemToMailboxPacket;
import net.jaju.subservermod.mailbox.network.packet.MailboxPacket;
import net.jaju.subservermod.mailbox.network.packet.UpdateMailboxPacket;
import net.jaju.subservermod.shopsystem.network.ShopEntityDataPacket;
import net.jaju.subservermod.shopsystem.network.UpdateInventoryPacket;
import net.jaju.subservermod.shopsystem.network.UpdateShopEntityPacket;
import net.jaju.subservermod.subclass.network.*;
import net.jaju.subservermod.village.VillageHudPacket;
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
        INSTANCE.registerMessage(id++, giftGetPacket.class, giftGetPacket::encode, giftGetPacket::decode, giftGetPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, EncyclopediaPacket.class, EncyclopediaPacket::encode, EncyclopediaPacket::decode, EncyclopediaPacket::handle);
        INSTANCE.registerMessage(id++, MailboxPacket.class, MailboxPacket::encode, MailboxPacket::decode, MailboxPacket::handle);
        INSTANCE.registerMessage(id++, AuctionPacket.class, AuctionPacket::encode, AuctionPacket::decode, AuctionPacket::handle);
        INSTANCE.registerMessage(id++, CoinDataRequestFromShopPacket.class, CoinDataRequestFromShopPacket::toBytes, CoinDataRequestFromShopPacket::new, CoinDataRequestFromShopPacket::handle);
        INSTANCE.registerMessage(id++, CoinDataShopSyncPacket.class, CoinDataShopSyncPacket::toBytes, CoinDataShopSyncPacket::new, CoinDataShopSyncPacket::handle);
        INSTANCE.registerMessage(id++, CoinDataRequestFromAuctionPacket.class, CoinDataRequestFromAuctionPacket::toBytes, CoinDataRequestFromAuctionPacket::new, CoinDataRequestFromAuctionPacket::handle);
        INSTANCE.registerMessage(id++, CoinDataAuctionSyncPacket.class, CoinDataAuctionSyncPacket::toBytes, CoinDataAuctionSyncPacket::new, CoinDataAuctionSyncPacket::handle);
        INSTANCE.registerMessage(id++, CoinDataUpdatePacket.class, CoinDataUpdatePacket::toBytes, CoinDataUpdatePacket::new, CoinDataUpdatePacket::handle);
        INSTANCE.registerMessage(id++, ClassDataRequestFromInformationPacket.class, ClassDataRequestFromInformationPacket::toBytes, ClassDataRequestFromInformationPacket::new, ClassDataRequestFromInformationPacket::handle);
        INSTANCE.registerMessage(id++, ClassDataInformationSyncPacket.class, ClassDataInformationSyncPacket::toBytes, ClassDataInformationSyncPacket::new, ClassDataInformationSyncPacket::handle);
        INSTANCE.registerMessage(id++, UpdateMailboxPacket.class, UpdateMailboxPacket::encode, UpdateMailboxPacket::decode, UpdateMailboxPacket::handle);
        INSTANCE.registerMessage(id++, UpdateAuctionPacket.class, UpdateAuctionPacket::encode, UpdateAuctionPacket::decode, UpdateAuctionPacket::handle);
        INSTANCE.registerMessage(id++, CoinDataSyncPacket.class, CoinDataSyncPacket::encode, CoinDataSyncPacket::decode, CoinDataSyncPacket::handle);
        INSTANCE.registerMessage(id++, CommandExecutorPacket.class, CommandExecutorPacket::encode, CommandExecutorPacket::decode, CommandExecutorPacket::handle);
        INSTANCE.registerMessage(id++, AddItemToMailboxPacket.class, AddItemToMailboxPacket::toBytes, AddItemToMailboxPacket::new, AddItemToMailboxPacket::handle);
        INSTANCE.registerMessage(id++, TemporaryOpPacket.class, TemporaryOpPacket::toBytes, TemporaryOpPacket::new, TemporaryOpPacket::handle);
        INSTANCE.registerMessage(id++, TemporaryOpRequestPacket.class, TemporaryOpRequestPacket::toBytes, TemporaryOpRequestPacket::new, TemporaryOpRequestPacket::handle);
        INSTANCE.registerMessage(id++, TemporaryOpResponsePacket.class, TemporaryOpResponsePacket::toBytes, TemporaryOpResponsePacket::new, TemporaryOpResponsePacket::handle);
        INSTANCE.registerMessage(id++, ShopEntityPositionPacket.class, ShopEntityPositionPacket::encode, ShopEntityPositionPacket::decode, ShopEntityPositionPacket::handle);
        INSTANCE.registerMessage(id++, PlayerEntityPositionPacket.class, PlayerEntityPositionPacket::encode, PlayerEntityPositionPacket::decode, PlayerEntityPositionPacket::handle);

        INSTANCE.registerMessage(id++, VillageHudPacket.class, VillageHudPacket::toBytes, VillageHudPacket::new, VillageHudPacket::handle);

        INSTANCE.registerMessage(id++, UpdateShopEntityPacket.class, UpdateShopEntityPacket::toBytes, UpdateShopEntityPacket::new, UpdateShopEntityPacket::handle);
        INSTANCE.messageBuilder(CoinDataServerSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CoinDataServerSyncPacket::new)
                .encoder(CoinDataServerSyncPacket::toBytes)
                .consumerMainThread(CoinDataServerSyncPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetFlagPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetFlagPacket::decode)
                .encoder(SetFlagPacket::encode)
                .consumerMainThread(SetFlagPacket::handle)
                .add();
        INSTANCE.messageBuilder(UpdateOvenRecipePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(UpdateOvenRecipePacket::decode)
                .encoder(UpdateOvenRecipePacket::encode)
                .consumerMainThread(UpdateOvenRecipePacket::handle)
                .add();
        INSTANCE.messageBuilder(UpdateMiddleOvenRecipePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(UpdateMiddleOvenRecipePacket::decode)
                .encoder(UpdateMiddleOvenRecipePacket::encode)
                .consumerMainThread(UpdateMiddleOvenRecipePacket::handle)
                .add();
        INSTANCE.messageBuilder(UpdateWoodcuttingUnionRecipePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(UpdateWoodcuttingUnionRecipePacket::decode)
                .encoder(UpdateWoodcuttingUnionRecipePacket::encode)
                .consumerMainThread(UpdateWoodcuttingUnionRecipePacket::handle)
                .add();
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToPlayer(ServerPlayer player, MSG message) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
