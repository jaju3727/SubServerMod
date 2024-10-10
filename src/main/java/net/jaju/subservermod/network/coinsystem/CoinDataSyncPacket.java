package net.jaju.subservermod.network.coinsystem;

import net.jaju.subservermod.util.CoinData;
import net.jaju.subservermod.manager.CoinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.UUID;
import java.util.function.Supplier;

public class CoinDataSyncPacket {
    private final UUID playerUUID;
    private final CoinData coinData;

    public CoinDataSyncPacket(UUID playerUUID, CoinData coinData) {
        this.playerUUID = playerUUID;
        this.coinData = coinData;
    }

    public static void encode(CoinDataSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.playerUUID);
        buffer.writeNbt(packet.coinData.serializeNBT());
    }

    public static CoinDataSyncPacket decode(FriendlyByteBuf buffer) {
        UUID playerUUID = buffer.readUUID();
        CoinData coinData = new CoinData();
        coinData.deserializeNBT(buffer.readNbt());
        return new CoinDataSyncPacket(playerUUID, coinData);
    }

    public static void handle(CoinDataSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.getUUID().equals(packet.playerUUID)) {
                CoinManager.updateCoinData(packet.playerUUID, packet.coinData);
            }
        });
        context.setPacketHandled(true);
    }
}
