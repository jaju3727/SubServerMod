package net.jaju.subservermod.subclass.skill.chef.chefblock;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.block.ModBlockEntities;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.sound.SoundPlayer;
import net.jaju.subservermod.subclass.BaseClass;
import net.jaju.subservermod.subclass.ClassManagement;
import net.jaju.subservermod.subclass.skill.alchemist.brewing.BrewingBlockEntity;
import net.jaju.subservermod.subclass.skill.farmer.oven.OvenBlockEntity;
import net.jaju.subservermod.subclass.skill.fisherman.fishing_rod.FishingRodBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ChefBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ChefBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChefBlockEntity) {
                BaseClass chef = getChefInstance(player);

                if (chef == null || chef.getLevel() < 2) {
                    player.sendSystemMessage(Component.literal("조리대는 2차를 전직해야 열 수 있습니다"));
                    return InteractionResult.FAIL;
                }
                ItemStack heldItem = player.getMainHandItem();
                ChefBlockEntity chefBlockEntity = (ChefBlockEntity) blockEntity;
                List<ItemStack> itemStacks = new ArrayList<>(chefBlockEntity.getItemStacks());
                int cookingOilCount = chefBlockEntity.getCookingOilCount();
                int butterCount = chefBlockEntity.getButterCount();

                if (chefBlockEntity.getoverCooked()) {
                    chefBlockEntity.setoverCooked(false);
                    chefBlockEntity.setoverCookFlag(false);
                    return InteractionResult.PASS;
                }
                if (chefBlockEntity.getoverCookFlag()) {
                    for (ItemStack itemStack : itemStacks) {
                        if (itemStack.getItem() == ModItem.BEEF_STEAK.get()) {
                            ItemHandlerHelper.giveItemToPlayer(player, itemStack);
                            itemStacks.remove(itemStack);
                            chefBlockEntity.setItemStacks(itemStacks);
                            chefBlockEntity.setoverCooked(false);
                            chefBlockEntity.setoverCookFlag(false);
                            return InteractionResult.PASS;
                        } else if (itemStack.getItem() == ModItem.PORK_BELLY.get()) {
                            ItemHandlerHelper.giveItemToPlayer(player, itemStack);
                            itemStacks.remove(itemStack);
                            chefBlockEntity.setItemStacks(itemStacks);
                            chefBlockEntity.setoverCooked(false);
                            chefBlockEntity.setoverCookFlag(false);
                            return InteractionResult.PASS;
                        } else if (itemStack.getItem() == ModItem.ROAST_CHICKEN.get()) {
                            ItemHandlerHelper.giveItemToPlayer(player, itemStack);
                            itemStacks.remove(itemStack);
                            chefBlockEntity.setItemStacks(itemStacks);
                            chefBlockEntity.setoverCooked(false);
                            chefBlockEntity.setoverCookFlag(false);
                            return InteractionResult.PASS;
                        } else if (itemStack.getItem() == ModItem.LAMB_STEAK.get()) {
                            ItemHandlerHelper.giveItemToPlayer(player, itemStack);
                            itemStacks.remove(itemStack);
                            chefBlockEntity.setItemStacks(itemStacks);
                            chefBlockEntity.setoverCooked(false);
                            chefBlockEntity.setoverCookFlag(false);
                            return InteractionResult.PASS;
                        } else if (itemStack.getItem() == ModItem.FRIED_EGG.get()) {
                            ItemHandlerHelper.giveItemToPlayer(player, itemStack);
                            itemStacks.remove(itemStack);
                            chefBlockEntity.setItemStacks(itemStacks);
                            chefBlockEntity.setoverCooked(false);
                            chefBlockEntity.setoverCookFlag(false);
                            return InteractionResult.PASS;
                        }

                    }
                }

                if (heldItem.getItem() == ModItem.COOKING_OIL.get()){
                    if (cookingOilCount > 0 || butterCount > 0) {
                        player.sendSystemMessage(Component.literal("식용유나 버터가 이미 들어가 있는 상태입니다."));
                        return InteractionResult.PASS;
                    }
                    chefBlockEntity.setCookingOilCount(4);
                    chefBlockEntity.setTick(System.currentTimeMillis());

                } else if (heldItem.getItem() == ModItem.BUTTER.get()) {
                    if (cookingOilCount > 0 || butterCount > 0) {
                        player.sendSystemMessage(Component.literal("식용유나 버터가 이미 들어가 있는 상태입니다."));
                        return InteractionResult.PASS;
                    }
                    chefBlockEntity.setButterCount(3);
                }
                else if (heldItem.getItem() == Items.BEEF || heldItem.getItem() == Items.MUTTON) {
                    if (butterCount == 0) {
                        player.sendSystemMessage(Component.literal("버터를 먼저 넣어주세요."));
                        return InteractionResult.PASS;
                    }
                    try {
                        itemStacks.get(1);
                        player.sendSystemMessage(Component.literal("이미 요리를 시작했습니다."));
                        return InteractionResult.PASS;
                    } catch (Exception e) {}
                    chefBlockEntity.setcookGauge(4);
                    chefBlockEntity.setcookFlag(true);
                    chefBlockEntity.setCookTick(System.currentTimeMillis());
                    Vec3 vecPos = Vec3.atCenterOf(pos);
//                    SoundPlayer.playCustomSoundInRadius(level, vecPos, 7f, new ResourceLocation(Subservermod.MOD_ID, "frypan_sound"), 1.0f, 1.0f);

                }
                else if (heldItem.getItem() == Items.PORKCHOP || heldItem.getItem() == Items.EGG || heldItem.getItem() == Items.CHICKEN) {
                    if (cookingOilCount == 0) {
                        player.sendSystemMessage(Component.literal("식용유를 먼저 넣어주세요."));
                        return InteractionResult.PASS;
                    }
                    try {
                        itemStacks.get(1);
                        player.sendSystemMessage(Component.literal("이미 요리를 시작했습니다."));
                        return InteractionResult.PASS;
                    } catch (Exception e) {}

                    chefBlockEntity.setcookGauge(4);
                    chefBlockEntity.setcookFlag(true);
                    chefBlockEntity.setTick(System.currentTimeMillis());
                    chefBlockEntity.setCookTick(System.currentTimeMillis());
                    Vec3 vecPos = Vec3.atCenterOf(pos);
//                    SoundPlayer.playCustomSoundInRadius(level, vecPos, 7f, new ResourceLocation(Subservermod.MOD_ID, "frypan_sound"), 1.0f, 1.0f);

                } else {
                    player.sendSystemMessage(Component.literal("조리대에 넣을 수 없는 아이템입니다."));
                    return InteractionResult.PASS;
                }

                itemStacks.add(new ItemStack(heldItem.getItem(), 1));
                chefBlockEntity.setItemStacks(itemStacks);

                heldItem.shrink(1);
                player.setItemInHand(hand, heldItem);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChefBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.CHEF_BLOCK_ENTITY.get(), ChefBlockEntity::tick);
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

    private BaseClass getChefInstance(Player player) {
        String playerName = player.getName().getString();
        return ClassManagement.getClasses(playerName).get("Chef");
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChefBlockEntity) {
                popResource(level, pos, new ItemStack(this));
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
