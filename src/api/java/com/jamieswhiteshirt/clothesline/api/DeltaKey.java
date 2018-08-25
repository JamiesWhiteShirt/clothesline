package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public final class DeltaKey implements Comparable<DeltaKey> {
    private final BlockPos delta;

    public static DeltaKey between(BlockPos from, BlockPos to) {
        return new DeltaKey(to.subtract(from));
    }

    private DeltaKey(BlockPos delta) {
        this.delta = delta;
    }

    public BlockPos getDelta() {
        return delta;
    }

    public DeltaKey reverse() {
        return new DeltaKey(
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
        return Objects.equals(delta, deltaKey.delta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delta);
    }

    @Override
    public String toString() {
        return "DeltaKey{" +
            ", delta=" + delta +
            '}';
    }
}
