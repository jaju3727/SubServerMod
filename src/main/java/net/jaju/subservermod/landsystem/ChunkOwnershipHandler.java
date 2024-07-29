package net.jaju.subservermod.landsystem;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.landsystem.network.ClientPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.jaju.subservermod.item.ModItem.CONSTRUNTING_ALLOW;
import static net.jaju.subservermod.landsystem.LandManager.isOwner;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class ChunkOwnershipHandler {

    public static final LandManager landManager = new LandManager();
    private static Map<UUID, UUID> playerInChunk = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (EffectiveSide.get() == LogicalSide.SERVER) {
            Player player = event.getEntity();
            BlockPos pos = player.blockPosition();
            handleChunkClaim(player, player.level(), pos);
        }
    }

    private static void handleChunkClaim(Player player, Level world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ResourceKey<Level> worldKey = world.dimension();
        String chunkKey = LandManager.getChunkKey(worldKey, chunkPos);

        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.getItem() == CONSTRUNTING_ALLOW.get()) {
            UUID owner = landManager.getOwner(chunkKey);

            if (owner == null) {
                landManager.setOwner(chunkKey, player.getUUID());
                player.sendSystemMessage(Component.literal("Chunk claimed successfully!"));
                heldItem.shrink(1);  // 손에 들고 있는 아이템 1줄이기
            } else if (owner.equals(player.getUUID())) {
                player.sendSystemMessage(Component.literal("You already own this chunk."));
            } else {
                player.sendSystemMessage(Component.literal("This chunk is already owned by someone else."));
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level world = player.getCommandSenderWorld();
        BlockPos pos = event.getPos();
        ChunkPos chunkPos = new ChunkPos(pos);
        ResourceKey<Level> worldKey = world.dimension();
        String chunkKey = LandManager.getChunkKey(worldKey, chunkPos);
        UUID owner = landManager.getOwner(chunkKey);

        if (!isOwner(chunkKey, player) && owner != null) {
            player.sendSystemMessage(Component.literal("You don't have permission to break blocks in this chunk."));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            Level world = player.getCommandSenderWorld();
            BlockPos pos = event.getPos();
            ChunkPos chunkPos = new ChunkPos(pos);
            ResourceKey<Level> worldKey = world.dimension();
            String chunkKey = LandManager.getChunkKey(worldKey, chunkPos);
            UUID owner = landManager.getOwner(chunkKey);

            if (!isOwner(chunkKey, player) && owner != null) {
                player.sendSystemMessage(Component.literal("You don't have permission to place blocks in this chunk."));
                event.setCanceled(true);
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        playerInChunk = ClientPacketHandler.getPlayerInChunk();
        if (player != null) {
            UUID owner = playerInChunk.get(player.getUUID());
            if (owner != null) {
                renderOwnerFace(mc, owner, event.getGuiGraphics());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderOwnerFace(Minecraft mc, UUID owner, GuiGraphics guiGraphics) {
        AbstractClientPlayer ownerPlayer = (AbstractClientPlayer) mc.level.getPlayerByUUID(owner);
        if (ownerPlayer != null) {
            int size = 32;
            int x = (guiGraphics.guiWidth() - size)/2;
            int y = 5;
            guiGraphics.blit(ownerPlayer.getSkinTextureLocation(), x, y, size, size, 8, 8, 8, 8, 64, 64);
            String ownerName = ownerPlayer.getName().getString();
            Component textComponent = Component.literal(ownerName).setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(0xFFFFFF)));
            guiGraphics.drawCenteredString(mc.font, textComponent.getString() + "님의 땅", x + size/2, y + size, 0xFFFFFF);
        }
    }

}
