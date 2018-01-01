package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

public final class EdgeKey implements Comparable<EdgeKey> {
    private final BlockPos pos;
    // TODO: Use something other than atan2 to compare angles.
    // The only requirement is that this is a periodic strictly increasing value for comparison
    private final float angle;
    private final int length;

    public EdgeKey(BlockPos from, BlockPos to) {
        this(to, Measurements.calculateGlobalAngleY(from, to), Measurements.calculateDistance(from, to));
    }

    private EdgeKey(BlockPos pos, float angle, int length) {
        this.pos = pos;
        this.angle = angle;
        this.length = length;
    }

    public BlockPos getPos() {
        return pos;
    }

    public float getAngle() {
        return angle;
    }

    public int getLength() {
        return length;
    }

    public EdgeKey reverse(BlockPos from) {
        return new EdgeKey(from, Measurements.floorModAngle(angle + 180.0F), length);
    }

    @Override
    public int compareTo(EdgeKey o) {
        int angleComparison = Float.compare(angle, o.angle);
        if (angleComparison != 0) {
            return angleComparison;
        } else {
            int lengthComparison = Integer.compare(length, o.length);
            if (lengthComparison != 0) {
                return lengthComparison;
            } else {
                return pos.compareTo(o.pos);
            }
        }
    }
}
