package net.jaju.subservermod.subclass.skill.alchemist.teleport;

import net.jaju.subservermod.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportBlockEntity extends BlockEntity {
    private UUID playerUUID;
    public static final long TELEPORT_COOLDOWN = 1000 * 60 * 10;
    private static final Map<UUID, Long> teleportCooldowns = new HashMap<>();

    public TeleportBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TELEPORT_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TeleportBlockEntity entity) {

    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public static long getLastTeleportTime(UUID playerUUID) {
        return teleportCooldowns.getOrDefault(playerUUID, 0L);
    }

    public static void setLastTeleportTime(UUID playerUUID, long lastTeleportTime) {
        teleportCooldowns.put(playerUUID, lastTeleportTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("PlayerUUID")) {
            this.playerUUID = tag.getUUID("PlayerUUID");
        }
        long lastTeleportTime = tag.getLong("LastTeleportTime");
        if (playerUUID != null) {
            teleportCooldowns.put(playerUUID, lastTeleportTime);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (playerUUID != null) {
            tag.putUUID("PlayerUUID", playerUUID);
            tag.putLong("LastTeleportTime", getLastTeleportTime(playerUUID));
        }
    }
}
