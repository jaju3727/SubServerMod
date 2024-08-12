package net.jaju.subservermod.subclass.skill.farmer.scarecrow_block;

import net.jaju.subservermod.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class ScareCrowBlockEntity extends BlockEntity {
    private static final int EFFECT_RADIUS = 5;
    private static final RandomSource randomSource = RandomSource.create();
    private static final Random random = new Random();

    public ScareCrowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCARECROW_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ScareCrowBlockEntity blockEntity) {
        if (!level.isClientSide && level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel) level;

            for (int x = -EFFECT_RADIUS; x <= EFFECT_RADIUS; x++) {
                for (int y = -EFFECT_RADIUS; y <= EFFECT_RADIUS; y++) {
                    for (int z = -EFFECT_RADIUS; z <= EFFECT_RADIUS; z++) {
                        BlockPos currentPos = pos.offset(x, y, z);
                        BlockState cropState = serverLevel.getBlockState(currentPos);
                        Block cropBlock = cropState.getBlock();
                        if (random.nextFloat(0.0f, 1.0f) >= 0.000045776 * 2) continue;
                        if (cropBlock instanceof CropBlock) {
                            CropBlock crop = (CropBlock) cropBlock;
                            for (int i = 0; i < 10; i++) {
                                crop.randomTick(cropState, serverLevel, currentPos, randomSource);
                            }
                        } else if (cropBlock instanceof StemBlock) {
                            StemBlock stem = (StemBlock) cropBlock;
                            for (int i = 0; i < 10; i++) {
                                stem.randomTick(cropState, serverLevel, currentPos, randomSource);
                            }
                        }
                    }
                }
            }
        }
    }
}
