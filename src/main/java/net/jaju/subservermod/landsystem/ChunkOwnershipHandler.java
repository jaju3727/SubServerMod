package net.jaju.subservermod.landsystem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.landsystem.network.ClientPacketHandler;
import net.jaju.subservermod.village.VillageProtectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
import static net.jaju.subservermod.landsystem.LandManager.isSharedWith;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class ChunkOwnershipHandler {
    private static Map<UUID, UUID> playerInChunk = new HashMap<>();
    public static final LandManager landManager = new LandManager();


    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            landManager.addPlayerNameCache(player.getUUID(), player.getName().getString());
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (EffectiveSide.get() == LogicalSide.SERVER) {
            Player player = event.getEntity();
            BlockPos pos = player.blockPosition();
            handleChunkClaim(player, player.level(), pos);
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (EffectiveSide.get() == LogicalSide.SERVER) {
            Player player = event.getEntity();
            BlockPos pos = event.getPos();
            Level world = player.level();
            ChunkPos chunkPos = new ChunkPos(pos);
            ResourceKey<Level> worldKey = world.dimension();
            String chunkKey = LandManager.getChunkKey(worldKey, chunkPos, pos.getY());
            UUID owner = landManager.getOwner(chunkKey);

            if ((!isOwner(chunkKey, player) && !isSharedWith(chunkKey, player)) && owner != null) {
                if (landManager.getY(chunkKey) - pos.getY() <= 8) {
                    player.sendSystemMessage(Component.literal("이 청크에 대한 권한이 없습니다."));
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                    return;
                }
            }

            handleChunkClaim(player, world, pos);
        }
    }

    private static void handleChunkClaim(Player player, Level world, BlockPos pos) {
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.getItem() == CONSTRUNTING_ALLOW.get()) {
            if (VillageProtectionManager.isInProtectedVillage3(pos, world)) {
                player.sendSystemMessage(Component.literal("이 청크는 마을에 포함되어 있어 소유할 수 없습니다."));
                return;
            }

            ChunkPos chunkPos = new ChunkPos(pos);
            ResourceKey<Level> worldKey = world.dimension();
            String chunkKey = LandManager.getChunkKey(worldKey, chunkPos, (int) player.getY());

            UUID owner = landManager.getOwner(chunkKey);

            if (owner == null) {
                landManager.setOwner(chunkKey, player.getUUID());
                player.sendSystemMessage(Component.literal(chunkKey + "에 권한 설정을 완료하였습니다."));
                heldItem.shrink(1);
                landManager.addPlayerNameCache(player.getUUID(), player.getName().getString());
            } else if (owner.equals(player.getUUID())) {
                player.sendSystemMessage(Component.literal("이미 청크를 소유하셨습니다."));
            } else {
                player.sendSystemMessage(Component.literal("이 청크는 다른 사람의 소유입니다."));
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
        String chunkKey = LandManager.getChunkKey(worldKey, chunkPos, pos.getY());
        UUID owner = landManager.getOwner(chunkKey);

        if ((!isOwner(chunkKey, player) && !isSharedWith(chunkKey, player)) && owner != null) {
            if (landManager.getY(chunkKey) - pos.getY() <= 8) {
                player.sendSystemMessage(Component.literal("이 청크에 대한 권한이 없습니다."));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            Level world = player.getCommandSenderWorld();
            BlockPos pos = event.getPos();
            ChunkPos chunkPos = new ChunkPos(pos);
            ResourceKey<Level> worldKey = world.dimension();
            String chunkKey = LandManager.getChunkKey(worldKey, chunkPos, pos.getY());
            UUID owner = landManager.getOwner(chunkKey);

            if ((!isOwner(chunkKey, player) && !isSharedWith(chunkKey, player)) && owner != null) {
                if (landManager.getY(chunkKey) - pos.getY() <= 8) {
                    player.sendSystemMessage(Component.literal("이 청크에 대한 권한이 없습니다."));
                    event.setCanceled(true);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null) {
            playerInChunk = ClientPacketHandler.getPlayerInChunk();
            UUID ownerUUID = playerInChunk.get(player.getUUID());
            if (ownerUUID != null) {
                BlockPos pos = player.blockPosition();
                ChunkPos chunkPos = new ChunkPos(pos);
                ResourceKey<Level> worldKey = player.level().dimension();
                String chunkKey = LandManager.getChunkKey(worldKey, chunkPos, pos.getY());
                if (landManager.getY(chunkKey) - pos.getY() <= 8) {
                    fetchAndRenderOwnerData(mc, ownerUUID, event.getGuiGraphics(), player);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void fetchAndRenderOwnerData(Minecraft mc, UUID ownerUUID, GuiGraphics guiGraphics, Player player) {
        String playerName = ClientPacketHandler.getOwnerName();
        renderOwnerFace(mc, playerName, guiGraphics);
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderOwnerFace(Minecraft mc, String playerName, GuiGraphics guiGraphics) {
        PoseStack poseStack = guiGraphics.pose();

        int x = guiGraphics.guiWidth() / 2;
        int y = 5;

        float scale = 1.9f;

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        int adjustedX = (int) (x / scale);
        int adjustedY = (int) (y / scale);

        guiGraphics.drawCenteredString(mc.font, playerName + "님의 땅", adjustedX, adjustedY, 0xA4A4A4);

        poseStack.popPose();
    }
}
