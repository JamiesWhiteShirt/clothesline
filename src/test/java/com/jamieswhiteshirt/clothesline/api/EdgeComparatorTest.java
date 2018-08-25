package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EdgeComparatorTest {
    void assertStrictlyOrdered(BlockPos[] vecs) {
        for (int i = 1; i < vecs.length; i++) {
            for (int j = 0; j < vecs.length; j++) {
                BlockPos a = vecs[i];
                BlockPos b = vecs[j];
                int c = EdgeComparator.getInstance().compare(a, b);
                if (i < j) {
                    Assertions.assertTrue(c < 0, "Expected " + a + " to be less than " + b);
                } else if (i > j) {
                    Assertions.assertTrue(c > 0, "Expected " + a + " to be greater than " + b);
                } else {
                    Assertions.assertEquals(0, c, "Expected " + a + " to equal " + b);
                }
            }
        }
    }

    @Test
    void compareXYAngleThenXYLengthThenY() {
        assertStrictlyOrdered(new BlockPos[] {
            new BlockPos(0, 0, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(1, 1, 0),
            new BlockPos(2, 0, 0),
            new BlockPos(2, 1, 0),
            new BlockPos(1, 0, 1),
            new BlockPos(1, 1, 1),
            new BlockPos(2, 0, 2),
            new BlockPos(2, 1, 2),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 0, 2),
            new BlockPos(0, 1, 2),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 1, 1),
            new BlockPos(-2, 0, 2),
            new BlockPos(-2, 1, 2),
            new BlockPos(-1, 0, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(-2, 0, 0),
            new BlockPos(-2, 1, 0),
            new BlockPos(-1, 0, -1),
            new BlockPos(-1, 1, -1),
            new BlockPos(-2, 0, -2),
            new BlockPos(-2, 1, -2),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 1, -1),
            new BlockPos(0, 0, -2),
            new BlockPos(0, 1, -2),
            new BlockPos(1, 0, -1),
            new BlockPos(1, 1, -1),
            new BlockPos(2, 0, -2),
            new BlockPos(2, 1, -2)
        });
    }
}
