package net.jaju.subservermod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Village {
    private final String name;
    private final BlockPos pos1;
    private final BlockPos pos2;
    private final boolean protectable;

    public Village(String name, BlockPos pos1, BlockPos pos2, boolean protectable) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.protectable = protectable;
    }

    public String getName() {
        return name;
    }

    public BlockPos getPos1() {
        return pos1;
    }

    public BlockPos getPos2() {
        return pos2;
    }

    public boolean getProtectable() {
        return protectable;
    }

    public boolean isWithinBounds(BlockPos pos, Level level) {
        boolean isWithinBounds = pos.getX() >= Math.min(pos1.getX(), pos2.getX())
                && pos.getX() <= Math.max(pos1.getX(), pos2.getX())
                && pos.getY() >= Math.min(pos1.getY(), pos2.getY())
                && pos.getY() <= Math.max(pos1.getY(), pos2.getY())
                && pos.getZ() >= Math.min(pos1.getZ(), pos2.getZ())
                && pos.getZ() <= Math.max(pos1.getZ(), pos2.getZ());

        return isWithinBounds;
    }

    public boolean isWithinBounds2(BlockPos pos, Level level) {
        boolean isWithinBounds = pos.getX() >= Math.min(pos1.getX(), pos2.getX())
                && pos.getX() <= Math.max(pos1.getX(), pos2.getX())
                && pos.getZ() >= Math.min(pos1.getZ(), pos2.getZ())
                && pos.getZ() <= Math.max(pos1.getZ(), pos2.getZ());

        return isWithinBounds;
    }
}
