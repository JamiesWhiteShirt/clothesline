package com.jamieswhiteshirt.clothesline.api;

import com.google.common.base.Preconditions;
import com.jamieswhiteshirt.rtree3i.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public final class Line {
    private final BlockPos from;
    private final BlockPos to;

    public Line(BlockPos from, BlockPos to) {
        Preconditions.checkArgument(!Objects.equals(from, to));
        this.from = from;
        this.to = to;
    }

    public BlockPos getFromPos() {
        return from;
    }

    public BlockPos getToPos() {
        return to;
    }

    public Vec3d getFromVec() {
        return Measurements.midVec(from);
    }

    public Vec3d getToVec() {
        return Measurements.midVec(to);
    }

    public Vec3d getPosition(double scalar) {
        return getFromVec().scale(1.0D - scalar).add(getToVec().scale(scalar));
    }

    public Box getBox() {
        return Box.create(
            Math.min(from.getX(), to.getX()),
            Math.min(from.getY(), to.getY()),
            Math.min(from.getZ(), to.getZ()),
            Math.max(from.getX(), to.getX()) + 1,
            Math.max(from.getY(), to.getY()) + 1,
            Math.max(from.getZ(), to.getZ()) + 1
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(from, line.from) &&
            Objects.equals(to, line.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "Line{" +
            "from=" + from +
            ", to=" + to +
            '}';
    }
}
