package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public final class DeltaKey implements Comparable<DeltaKey> {
    private final int length;
    private final BlockPos delta;

    public static DeltaKey between(BlockPos from, BlockPos to) {
        return new DeltaKey(Measurements.calculateDistance(from, to), to.subtract(from));
    }

    private DeltaKey(int length, BlockPos delta) {
        this.length = length;
        this.delta = delta;
    }

    public int getLength() {
        return length;
    }

    public BlockPos getDelta() {
        return delta;
    }

    public DeltaKey reverse() {
        return new DeltaKey(
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
        return length == deltaKey.length &&
            Objects.equals(delta, deltaKey.delta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, delta);
    }

    @Override
    public String toString() {
        return "DeltaKey{" +
            "length=" + length +
            ", delta=" + delta +
            '}';
    }
}
