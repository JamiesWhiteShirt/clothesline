package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public final class DeltaKey implements Comparable<DeltaKey> {
    // TODO: Use something other than atan2 to compare angles.
    // The only requirement is that this is a periodic strictly increasing value for comparison
    private final float angle;
    private final int length;
    private final BlockPos delta;

    public static DeltaKey between(BlockPos from, BlockPos to) {
        return new DeltaKey(Measurements.calculateGlobalAngleY(from, to), Measurements.calculateDistance(from, to), to.subtract(from));
    }

    private DeltaKey(float angle, int length, BlockPos delta) {
        this.angle = angle;
        this.length = length;
        this.delta = delta;
    }

    public float getAngle() {
        return angle;
    }

    public int getLength() {
        return length;
    }

    public BlockPos getDelta() {
        return delta;
    }

    public DeltaKey reverse() {
        return new DeltaKey(
            // if delta.x == 0 && delta.z == 0, then the angle is undefined and cannot be reversed
            delta.getX() != 0 || delta.getZ() != 0 ? Measurements.floorModAngle(angle + 180.0F) : angle,
            length,
            BlockPos.ORIGIN.subtract(delta)
        );
    }

    @Override
    public int compareTo(DeltaKey o) {
        return EdgeComparator.getInstance().compare(delta, o.delta);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeltaKey deltaKey = (DeltaKey) o;
        return Float.compare(deltaKey.angle, angle) == 0 &&
            length == deltaKey.length &&
            Objects.equals(delta, deltaKey.delta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(angle, length, delta);
    }

    @Override
    public String toString() {
        return "DeltaKey{" +
            "angle=" + angle +
            ", length=" + length +
            ", delta=" + delta +
            '}';
    }
}
