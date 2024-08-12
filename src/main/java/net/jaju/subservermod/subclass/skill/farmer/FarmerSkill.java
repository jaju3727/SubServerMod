package net.jaju.subservermod.subclass.skill.farmer;

import net.jaju.subservermod.block.ModBlocks;
import net.jaju.subservermod.subclass.Farmer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.core.Direction;

import java.util.Random;

@Mod.EventBusSubscriber
public class FarmerSkill {
    private final Farmer farmer;

    public FarmerSkill(Farmer farmer) {
        this.farmer = farmer;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onSeedPlant(PlayerInteractEvent.RightClickBlock event) {
        if (farmer == null || farmer.getLevel() < 2) {
            return;
        }

        if (event.getLevel() instanceof ServerLevel) {
            ItemStack itemStack = event.getItemStack();
            Item item = itemStack.getItem();
            BlockPos pos = event.getPos();
            ServerLevel world = (ServerLevel) event.getLevel();
            Direction playerFacing = event.getEntity().getDirection();

            BlockPos leftPos = pos.relative(playerFacing.getClockWise());
            BlockPos rightPos = pos.relative(playerFacing.getCounterClockWise());

            if (item == Items.NETHER_WART) {
                if (canPlantNetherWart(world, leftPos) && itemStack.getCount() > 0 && !isSeedPlanted(world, leftPos.above())) {
                    world.setBlock(leftPos.above(), Blocks.NETHER_WART.defaultBlockState(), 3);
                    itemStack.shrink(1);
                }
                if (canPlantNetherWart(world, rightPos) && itemStack.getCount() > 0 && !isSeedPlanted(world, rightPos.above())) {
                    world.setBlock(rightPos.above(), Blocks.NETHER_WART.defaultBlockState(), 3);
                    itemStack.shrink(1);
                }
            } else if (item == Items.WHEAT_SEEDS || item == Items.POTATO || item == Items.CARROT || item == Items.BEETROOT_SEEDS ||
                    item == Items.PUMPKIN_SEEDS || item == Items.MELON_SEEDS || item == Items.SUGAR_CANE) {
                if (canPlantSeed(world, leftPos) && itemStack.getCount() > 0 && !isSeedPlanted(world, leftPos.above())) {
                    world.setBlock(leftPos.above(), getSeedBlockState(item), 3);
                    itemStack.shrink(1);
                }
                if (canPlantSeed(world, rightPos) && itemStack.getCount() > 0 && !isSeedPlanted(world, rightPos.above())) {
                    world.setBlock(rightPos.above(), getSeedBlockState(item), 3);
                    itemStack.shrink(1);
                }
            }
        }
    }

    private boolean canPlantNetherWart(ServerLevel world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.getBlock() == Blocks.SOUL_SAND;
    }

    private boolean canPlantSeed(ServerLevel world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.getBlock() == Blocks.FARMLAND && world.isEmptyBlock(pos.above());
    }

    private boolean isSeedPlanted(ServerLevel world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block instanceof CropBlock || block == Blocks.NETHER_WART ||
                block == Blocks.PUMPKIN_STEM || block == Blocks.MELON_STEM ||
                block == Blocks.SUGAR_CANE;
    }

    private BlockState getSeedBlockState(Item item) {
        if (item == Items.WHEAT_SEEDS) {
            return Blocks.WHEAT.defaultBlockState();
        } else if (item == Items.POTATO) {
            return Blocks.POTATOES.defaultBlockState();
        } else if (item == Items.CARROT) {
            return Blocks.CARROTS.defaultBlockState();
        } else if (item == Items.BEETROOT_SEEDS) {
            return Blocks.BEETROOTS.defaultBlockState();
        } else if (item == Items.PUMPKIN_SEEDS) {
            return Blocks.PUMPKIN_STEM.defaultBlockState();
        } else if (item == Items.MELON_SEEDS) {
            return Blocks.MELON_STEM.defaultBlockState();
        } else if (item == Items.SUGAR_CANE) {
            return Blocks.SUGAR_CANE.defaultBlockState();
        } else {
            return Blocks.AIR.defaultBlockState(); // Fallback case
        }
    }

    @SubscribeEvent
    public void onHarvestCrop(BlockEvent.BreakEvent event) {
        if (farmer == null || farmer.getLevel() < 2) {
            return;
        }

        if (event.getLevel() instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) event.getLevel();
            BlockPos pos = event.getPos();
            BlockState blockState = event.getState();
            Player player = event.getPlayer();

            Block block = blockState.getBlock();
            if (block instanceof CropBlock || block == Blocks.NETHER_WART ||
                    block == Blocks.PUMPKIN || block == Blocks.MELON ||
                    block == Blocks.SUGAR_CANE) {
                if (block instanceof CropBlock) {
                    CropBlock crop = (CropBlock) block;
                    if (!crop.isMaxAge(blockState)) {
                        return;
                    }
                }

                Direction playerFacing = player.getDirection();

                BlockPos leftPos = pos.relative(playerFacing.getClockWise());
                BlockPos rightPos = pos.relative(playerFacing.getCounterClockWise());

                if (world.getBlockState(leftPos).getBlock() == block) {
                    world.destroyBlock(leftPos, true, player);
                }
                if (world.getBlockState(rightPos).getBlock() == block) {
                    world.destroyBlock(rightPos, true, player);
                }
            }
        }
    }
}
