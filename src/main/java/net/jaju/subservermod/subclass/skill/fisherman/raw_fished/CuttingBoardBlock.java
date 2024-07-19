package net.jaju.subservermod.subclass.skill.fisherman.raw_fished;

import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.subclass.skill.chef.chefblock.ChefBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class CuttingBoardBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CuttingBoardBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CuttingBoardBlockEntity) {
                ItemStack heldItem = player.getMainHandItem();
                ItemStack offItem = player.getOffhandItem();
                CuttingBoardBlockEntity cuttingBoardBlockEntity = (CuttingBoardBlockEntity) blockEntity;
                Item item = cuttingBoardBlockEntity.getItem();

                if (item == ModItem.COD_RAW_FISH.get() || item == ModItem.SALMON_RAW_FISH.get()) {
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(item, 1));
                    cuttingBoardBlockEntity.setItem(Items.AIR);
                    return InteractionResult.PASS;
                }

                if (!(heldItem.getItem() == Items.COD || heldItem.getItem() == Items.SALMON || offItem.getItem() == Items.COD || offItem.getItem() == Items.SALMON)) {
                    player.sendSystemMessage(Component.literal("도마에 올릴 수 없는 아이템입니다."));
                    return InteractionResult.PASS;
                }

                if (!(item == Items.COD || item == Items.SALMON)) {
                    if (heldItem.getItem() == Items.COD || heldItem.getItem() == Items.SALMON) {
                        cuttingBoardBlockEntity.setItem(heldItem.getItem());
                        heldItem.shrink(1);
                        player.setItemInHand(hand, heldItem);
                    }
                    else {
                        cuttingBoardBlockEntity.setItem(offItem.getItem());
                        offItem.shrink(1);
                        player.setItemInHand(InteractionHand.OFF_HAND, offItem);
                    }
                    cuttingBoardBlockEntity.setClickCount(10);

                    return InteractionResult.SUCCESS;
                } else {
                    player.sendSystemMessage(Component.literal("이미 도마에 물고기가 올라가 있습니다."));
                    return InteractionResult.PASS;
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    @SubscribeEvent
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CuttingBoardBlockEntity) {
                CuttingBoardBlockEntity cuttingBoardBlockEntity = (CuttingBoardBlockEntity) blockEntity;
                int clickCount = cuttingBoardBlockEntity.getClickCount();
                long clickTime = System.currentTimeMillis() - cuttingBoardBlockEntity.getClickTime();

                if (((clickCount > 0 && clickTime > 80) || clickCount == 10) && heldItem.getItem() == ModItem.SASHIMI_KNIFE.get()) {
                    cuttingBoardBlockEntity.setClickCount(clickCount - 1);
                    heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(event.getHand()));
                    cuttingBoardBlockEntity.setClickTime(System.currentTimeMillis());
                    player.getInventory().setChanged();
                    event.setCanceled(true);
                }
            }
        }
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.CUTTING_BOARD_BLOCK_ENTITY.get(), CuttingBoardBlockEntity::tick);
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
