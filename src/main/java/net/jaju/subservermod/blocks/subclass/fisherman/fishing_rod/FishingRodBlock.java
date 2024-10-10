package net.jaju.subservermod.blocks.subclass.fisherman.fishing_rod;

import net.jaju.subservermod.blocks.ModBlockEntities;
import net.jaju.subservermod.manager.ClassManager;
import net.jaju.subservermod.subclass.BaseClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class FishingRodBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public FishingRodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof FishingRodBlockEntity fishingRodBlockEntity) {
                BaseClass fisherman = getFishermanInstance(player);

                if (fisherman == null || fisherman.getLevel() < 2) {
                    player.sendSystemMessage(Component.literal("도마는 2차를 전직해야 열 수 있습니다"));
                    return InteractionResult.FAIL;
                }
                ItemStack heldItem = player.getMainHandItem();
                ItemStack itemStack = fishingRodBlockEntity.getItemStack();

                if (!fishingRodBlockEntity.isCatchFish()) {
                    if (heldItem.getItem() == Items.FISHING_ROD) {
                        if (itemStack.getItem() == Items.AIR) {
                            fishingRodBlockEntity.setItemStack(heldItem.copy());
                            fishingRodBlockEntity.setFishingTick(System.currentTimeMillis());
                        } else {
                            player.sendSystemMessage(Component.literal("이미 낚싯대를 등록하셨습니다."));
                            return InteractionResult.SUCCESS;
                        }
                    } else {
                        if (itemStack.getItem() == Items.AIR) {
                            player.sendSystemMessage(Component.literal("낚싯대를 들고 우클릭을 해주세요."));
                            return InteractionResult.SUCCESS;
                        } else {
                            ItemHandlerHelper.giveItemToPlayer(player, fishingRodBlockEntity.getItemStack());
                            fishingRodBlockEntity.setItemStack(new ItemStack(Items.AIR));
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else {
                    fishingRodBlockEntity.setCatchFish(false);
                    fishingRodBlockEntity.setFishingTick(System.currentTimeMillis());
                    itemStack.hurt(1, RandomSource.create(), (ServerPlayer) player);
                    if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                        itemStack = new ItemStack(Items.AIR);
                    }
                    fishingRodBlockEntity.setItemStack(itemStack);
                    ItemHandlerHelper.giveItemToPlayer(player, getFishingLoot());
                    return InteractionResult.SUCCESS;
                }

                heldItem.shrink(1);
                player.setItemInHand(hand, heldItem);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.CONSUME;
    }

    private static ItemStack getFishingLoot() {
        Random random = new Random();
        int chance = random.nextInt(100);

        if (chance < 85) { // 85% 확률로 물고기
            return getRandomFishLoot(random);
        } else if (chance < 95) { // 10% 확률로 쓰레기
            return getRandomJunkLoot(random);
        } else { // 5% 확률로 보물
            return getRandomTreasureLoot(random);
        }
    }

    private static ItemStack getRandomFishLoot(Random random) {
        List<ItemStack> fishLoot = Arrays.asList(
                new ItemStack(Items.COD),
                new ItemStack(Items.SALMON),
                new ItemStack(Items.TROPICAL_FISH),
                new ItemStack(Items.PUFFERFISH)
        );
        List<Integer> chance = Arrays.asList(
                60, 85, 87, 100
        );

        int randomInt = random.nextInt(100);

        for (int i = 0; i < fishLoot.size(); i++) if (randomInt < chance.get(i)) return fishLoot.get(i);

        return null;
    }

    private static ItemStack getRandomJunkLoot(Random random) {
        List<ItemStack> junkLoot = Arrays.asList(
                new ItemStack(Items.BOWL),
                new ItemStack(Items.LEATHER_BOOTS),
                new ItemStack(Items.ROTTEN_FLESH),
                new ItemStack(Items.STRING),
                new ItemStack(Items.FISHING_ROD),
                new ItemStack(Items.BONE),
                PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER),
                new ItemStack(Items.INK_SAC),
                new ItemStack(Items.TRIPWIRE_HOOK)
        );
        return junkLoot.get(random.nextInt(junkLoot.size()));
    }

    private static ItemStack getRandomTreasureLoot(Random random) {
        List<ItemStack> treasureLoot = Arrays.asList(
                new ItemStack(Items.NAME_TAG),
                new ItemStack(Items.SADDLE),
                new ItemStack(Items.NAUTILUS_SHELL)
        );
        return treasureLoot.get(random.nextInt(treasureLoot.size()));
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FishingRodBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.FISHING_ROD_BLOCK_ENTITY.get(), FishingRodBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

        for (Direction direction : directions) {
            BlockPos adjacentPos = pos.relative(direction).below();
            if (level.getBlockState(adjacentPos).is(Blocks.WATER)) {
                return this.defaultBlockState().setValue(FACING, direction);
            }
        }

        return null;
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

    private BaseClass getFishermanInstance(Player player) {
        String playerName = player.getName().getString();
        return ClassManager.getClasses(playerName).get("Fisherman");
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof FishingRodBlockEntity) {
                FishingRodBlockEntity brewingBlockEntity = (FishingRodBlockEntity) blockEntity;
                popResource(level, pos, brewingBlockEntity.getItemStack());
                popResource(level, pos, new ItemStack(this));
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
