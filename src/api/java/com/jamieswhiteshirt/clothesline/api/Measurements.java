package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Measurements {
    public static final int UNIT_LENGTH = 160;

    public static Vec3d midVec(BlockPos pos) {
        return new Vec3d(pos).addVector(0.5D, 0.5D, 0.5D);
    }

    public static float floorModAngle(float angle) {
        if (angle >= 0.0F) {
            return angle % 360.0F;
        } else {
            return 360.0F + (angle % 360.0F);
        }
    }

    public static float calculateAngleBetween(BlockPos origin, BlockPos posA, BlockPos posB) {
        return floorModAngle(calculateGlobalAngle(origin, posB) - calculateGlobalAngle(origin, posA));
    }

    public static float calculateGlobalAngle(BlockPos from, BlockPos to) {
        return floorModAngle((float)StrictMath.toDegrees(Math.atan2(to.getZ() - from.getZ(), to.getX() - from.getX())));
    }

    public static int calculateDistance(BlockPos from, BlockPos to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return (int)(UNIT_LENGTH * StrictMath.sqrt(dx * dx + dy * dy + dz * dz));
    }

    /**
     * Compare two block positions relative to a basis position.
     * Compares the angle of posA relative to the basis position and posB relative to the basis position.
     * If the angle comparison is nonzero, the return value is the angle comparison.
     * Otherwise, the return value is posA compared to posB.
     * @param origin
     * @param basis
     * @param posA
     * @param posB
     * @return
     */
    public static int compare(BlockPos origin, BlockPos basis, BlockPos posA, BlockPos posB) {
        int angleComparison = Float.compare(calculateAngleBetween(origin, basis, posA), calculateAngleBetween(origin, basis, posB));
        if (angleComparison == 0) {
            return posA.compareTo(posB);
        } else {
            return angleComparison;
        }
    }

    public static int compareGlobal(BlockPos origin, BlockPos posA, BlockPos posB) {
        int angleComparison = Float.compare(calculateGlobalAngle(origin, posA), calculateGlobalAngle(origin, posB));
        if (angleComparison == 0) {
            return posA.compareTo(posB);
        } else {
            return angleComparison;
        }
    }
}
