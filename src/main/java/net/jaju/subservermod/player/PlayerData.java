package net.jaju.subservermod.player;

import net.jaju.subservermod.Subservermod;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

@Mod.EventBusSubscriber
public class PlayerData {
    private final ItemStackHandler extraInventory;

    public PlayerData(int slots) {
        this.extraInventory = new ItemStackHandler(slots);
    }

    public ItemStackHandler getExtraInventory() {
        return extraInventory;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("ExtraInventory", extraInventory.serializeNBT());
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        extraInventory.deserializeNBT(nbt.getCompound("ExtraInventory"));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PlayerData original = PlayerDataProvider.get(event.getOriginal());
            PlayerData clone = PlayerDataProvider.get(event.getEntity());
            if (original != null && clone != null) {
                clone.deserializeNBT(original.serializeNBT());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerData playerData = PlayerDataProvider.get(event.getEntity());
        if (playerData != null) {
            playerData.deserializeNBT(event.getEntity().getPersistentData().getCompound(Subservermod.MOD_ID));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerData playerData = PlayerDataProvider.get(event.getEntity());
        if (playerData != null) {
            event.getEntity().getPersistentData().put(Subservermod.MOD_ID, playerData.serializeNBT());
        }
    }
}