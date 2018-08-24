package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Measurements {
    public static final int UNIT_LENGTH = 160;

    public static Vec3d midVec(BlockPos pos) {
        return new Vec3d(pos).add(0.5D, 0.5D, 0.5D);
    }

    public static float floorModAngle(float angle) {
        if (angle >= 0.0F) {
            return angle % 360.0F;
        } else {
            return 360.0F + (angle % 360.0F);
        }
    }

    public static float calculateGlobalAngleY(BlockPos from, BlockPos to) {
        return floorModAngle((float)StrictMath.toDegrees(Math.atan2(to.getZ() - from.getZ(), to.getX() - from.getX())));
    }

    public static int calculateDistance(BlockPos from, BlockPos to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return (int)(UNIT_LENGTH * StrictMath.sqrt(dx * dx + dy * dy + dz * dz));
    }
}
