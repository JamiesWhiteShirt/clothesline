package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

public class Node {
    private final BlockPos pos;
    private final int offset;
    private final float angleY;

    public Node(BlockPos pos, int offset, float angleY) {
        this.pos = pos;
        this.offset = offset;
        this.angleY = angleY;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getOffset() {
        return offset;
    }

    public float getAngleY() {
        return angleY;
    }

    public Node addOffset(int offset) {
        return new Node(pos, this.offset + offset, angleY);
    }
}
