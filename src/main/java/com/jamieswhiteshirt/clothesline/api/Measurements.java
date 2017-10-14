package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

public class Measurements {
    public static final int UNIT_LENGTH = 160;

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
        return floorModAngle((float)Math.toDegrees(Math.atan2(to.getZ() - from.getZ(), to.getX() - from.getX())));
    }

    public static int calculateDistance(BlockPos from, BlockPos to) {
        return (int)(UNIT_LENGTH * from.getDistance(to.getX(), to.getY(), to.getZ()));
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
