package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AttachmentUnit {
    /**
     * The amount of attachment units per block
     */
    public static final int UNITS_PER_BLOCK = 160;

    /**
     * Returns the length between the positions in attachment units.
     * @param from first position
     * @param to second position
     * @return the length between the positions in attachment units
     */
    public static int lengthBetween(BlockPos from, BlockPos to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return (int)(UNITS_PER_BLOCK * StrictMath.sqrt(dx * dx + dy * dy + dz * dz));
    }
}
