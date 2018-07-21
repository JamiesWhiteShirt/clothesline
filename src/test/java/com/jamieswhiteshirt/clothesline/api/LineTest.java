package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LineTest {
    @Test
    void lineMustHaveLength() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Line(BlockPos.ORIGIN, BlockPos.ORIGIN));
    }

    @Test
    void vecMustBeCentered() {
        BlockPos fromPos = new BlockPos(1, 0, 0);
        Vec3d fromVec = new Vec3d(1.5, 0.5, 0.5);
        BlockPos toPos = new BlockPos(0, 1, 0);
        Vec3d toVec = new Vec3d(0.5, 1.5, 0.5);
        Line line = new Line(fromPos, toPos);
        Assertions.assertEquals(line.getFromPos(), fromPos);
        Assertions.assertEquals(line.getFromVec(), fromVec);
        Assertions.assertEquals(line.getToPos(), toPos);
        Assertions.assertEquals(line.getToVec(), toVec);
    }
}
