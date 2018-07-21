package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EdgeKeyTest {
    @Test
    void reverseEqualsReverseConstructed() {
        BlockPos posA = BlockPos.ORIGIN;
        BlockPos posB = new BlockPos(1, 1, 1);
        EdgeKey key1 = new EdgeKey(posA, posB);
        EdgeKey key2 = new EdgeKey(posB, posA);
        Assertions.assertEquals(key1.reverse(posA), key2);
    }

    @Test
    void compareAngle() {
        EdgeKey keyA = new EdgeKey(BlockPos.ORIGIN, new BlockPos(1, 0, 0));
        EdgeKey keyB = new EdgeKey(BlockPos.ORIGIN, new BlockPos(0, 0, 2));
        Assertions.assertTrue(keyA.getAngle() < keyB.getAngle());
        Assertions.assertTrue(keyA.compareTo(keyB) < 0);
    }

    @Test
    void compareAngleThenLength() {
        EdgeKey keyA = new EdgeKey(BlockPos.ORIGIN, new BlockPos(1, 0, 0));
        EdgeKey keyB = new EdgeKey(BlockPos.ORIGIN, new BlockPos(2, 0, 0));
        Assertions.assertEquals(keyA.getAngle(), keyB.getAngle());
        Assertions.assertTrue(keyA.getLength() < keyB.getLength());
        Assertions.assertTrue(keyA.compareTo(keyB) < 0);
    }

    @Test
    void compareAngleThenLengthThenBlockPos() {
        EdgeKey keyA = new EdgeKey(BlockPos.ORIGIN, new BlockPos(1, -1, 0));
        EdgeKey keyB = new EdgeKey(BlockPos.ORIGIN, new BlockPos(1, 1, 0));
        Assertions.assertEquals(keyA.getAngle(), keyB.getAngle());
        Assertions.assertEquals(keyA.getLength(), keyB.getLength());
        Assertions.assertTrue(keyA.getPos().compareTo(keyB.getPos()) < 0);
        Assertions.assertTrue(keyA.compareTo(keyB) < 0);
    }
}
