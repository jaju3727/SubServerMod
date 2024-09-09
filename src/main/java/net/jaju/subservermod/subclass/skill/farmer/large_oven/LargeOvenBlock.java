package net.jaju.subservermod.subclass.skill.farmer.large_oven;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.subclass.BaseClass;
import net.jaju.subservermod.subclass.ClassManagement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class LargeOvenBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public LargeOvenBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BaseClass farmer = getFarmerInstance(player);

            if (farmer == null || farmer.getLevel() < 3) {
                player.sendSystemMessage(Component.literal("대형 오븐은 3차를 전직해야 열 수 있습니다"));
                return InteractionResult.FAIL;
            }
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LargeOvenBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, (LargeOvenBlockEntity) blockEntity, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LargeOvenBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.LARGE_OVEN_BLOCK_ENTITY.get(), LargeOvenBlockEntity::tick);
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

    private BaseClass getFarmerInstance(Player player) {
        // 예제: 플레이어 이름을 기반으로 Farmer 인스턴스를 찾는 로직
        String playerName = player.getName().getString();
        return ClassManagement.getClasses(playerName).get("Farmer");
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LargeOvenBlockEntity) {
                LargeOvenBlockEntity ovenBlockEntity = (LargeOvenBlockEntity) blockEntity;
                for (int i = 0; i < ovenBlockEntity.getItemHandler().getSlots(); i++) {
                    ItemStack stack = ovenBlockEntity.getItemHandler().getStackInSlot(i);
                    while (!stack.isEmpty()) {
                        ItemStack singleStack = stack.split(1); // 한 번에 한 아이템씩 분리하여 드롭
                        popResource(level, pos, singleStack);
                    }
                }
                popResource(level, pos, new ItemStack(this));
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}