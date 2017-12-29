package com.jamieswhiteshirt.clothesline.common;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Util {
    /**
     * Forge really likes annotation magic. This makes static analysis tools shut up.
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> T nonNullInjected() {
        return null;
    }

    public static Vec3d midVec(BlockPos pos) {
        return new Vec3d(pos).addVector(0.5D, 0.5D, 0.5D);
    }
}
