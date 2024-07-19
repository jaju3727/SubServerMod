package net.jaju.subservermod.subclass.skill.alchemist.teleport;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.subclass.BaseClass;
import net.jaju.subservermod.subclass.ClassManagement;
import net.jaju.subservermod.subclass.skill.alchemist.brewing.BrewingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Mod.EventBusSubscriber
public class TeleportBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public TeleportBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TeleportBlockEntity teleportBlockEntity) {
                UUID playerUUID = player.getUUID();
                if (!playerUUID.equals(teleportBlockEntity.getPlayerUUID())) {
                    player.sendSystemMessage(Component.literal("당신은 이 블럭의 주인이 아닙니다."));
                    return InteractionResult.FAIL;
                }
                long currentTime = System.currentTimeMillis();
                long lastTeleportTime = TeleportBlockEntity.getLastTeleportTime(playerUUID);
                long restTime = currentTime - lastTeleportTime;
                if (restTime < TeleportBlockEntity.TELEPORT_COOLDOWN) {
                    restTime = TeleportBlockEntity.TELEPORT_COOLDOWN - restTime;
                    int minute = (int) (restTime/1000/60);
                    int second = (int) (restTime/1000)%60;
                    if (minute != 0) player.sendSystemMessage(Component.literal("텔레포트 쿨타임이 " + minute + "분 " + second + "초 남았습니다."));
                    else player.sendSystemMessage(Component.literal("텔레포트 쿨타임이 " + second + "초 남았습니다."));
                    return InteractionResult.FAIL;
                }

                teleportPlayer(level, player, teleportBlockEntity);
                TeleportBlockEntity.setLastTeleportTime(playerUUID, currentTime);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && placer instanceof Player) {
            Player player = (Player) placer;
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TeleportBlockEntity teleportBlockEntity) {
                UUID playerUUID = player.getUUID();
                teleportBlockEntity.setPlayerUUID(playerUUID);
                TeleportBlockEntity.setLastTeleportTime(playerUUID, 0);
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof TeleportBlock) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TeleportBlockEntity teleportBlockEntity) {
                UUID playerUUID = teleportBlockEntity.getPlayerUUID();
                long currentTime = System.currentTimeMillis();
                long lastTeleportTime = TeleportBlockEntity.getLastTeleportTime(playerUUID);

                if (currentTime - lastTeleportTime < TeleportBlockEntity.TELEPORT_COOLDOWN) {
                    player.sendSystemMessage(Component.literal("쿨타임 동안에는 텔레포트 블럭을 부술 수 없습니다."));
                    event.setCanceled(true);
                }
            }
        }
    }

    private void teleportPlayer(Level level, Player player, TeleportBlockEntity sourceBlockEntity) {
        BlockPos targetPos = findMatchingTeleportBlock(level, player.getUUID(), sourceBlockEntity.getBlockPos());
        if (targetPos != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.teleportTo(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);
            }
        } else {
            player.sendSystemMessage(Component.literal("연결된 텔레포트 블럭을 찾을 수 없습니다."));
        }
    }

    private BlockPos findMatchingTeleportBlock(Level level, UUID playerUUID, BlockPos excludePos) {
        for (BlockPos pos : BlockPos.betweenClosed(excludePos.offset(-100, -100, -100), excludePos.offset(100, 100, 100))) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TeleportBlockEntity teleportBlockEntity) {
                if (playerUUID.equals(teleportBlockEntity.getPlayerUUID()) && !pos.equals(excludePos)) {
                    return pos;
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeleportBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.TELEPORT_BLOCK_ENTITY.get(), TeleportBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
