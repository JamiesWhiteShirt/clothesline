package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Utility {
    public static Vec3d midVec(BlockPos pos) {
        return new Vec3d(pos).add(0.5D, 0.5D, 0.5D);
    }
}
