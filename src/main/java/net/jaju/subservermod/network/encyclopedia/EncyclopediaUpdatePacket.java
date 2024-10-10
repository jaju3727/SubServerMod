package net.jaju.subservermod.network.encyclopedia;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class EncyclopediaUpdatePacket {
    private final String itemName;
    private final Boolean itemFlag;
    private final Integer itemNum;

    public EncyclopediaUpdatePacket(String itemName, Boolean itemFlag, Integer itemNum) {
        this.itemName = itemName;
        this.itemNum = itemNum;
        this.itemFlag = itemFlag;
    }

    public static void encode(EncyclopediaUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.itemName);
        buf.writeInt(msg.itemNum);
        buf.writeBoolean(msg.itemFlag);
    }

    public static EncyclopediaUpdatePacket decode(FriendlyByteBuf buf) {
        return new EncyclopediaUpdatePacket(buf.readUtf(), buf.readBoolean(), buf.readInt());
    }

    public static void handle(EncyclopediaUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
//            ClientPacketHandler.handleEncyclopediaUpdate(msg.itemName, msg.itemFlag, msg.itemNum);
        });
        ctx.get().setPacketHandled(true);
    }
}

