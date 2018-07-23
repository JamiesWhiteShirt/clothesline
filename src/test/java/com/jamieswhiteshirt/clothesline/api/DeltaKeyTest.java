package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DeltaKeyTest {
    @Test
    void reverseEqualsReverseConstructed() {
        BlockPos posA = BlockPos.ORIGIN;
        BlockPos posB = new BlockPos(1, 1, 1);
        DeltaKey key1 = DeltaKey.between(posA, posB);
        DeltaKey key2 = DeltaKey.between(posB, posA);
        Assertions.assertEquals(key1.reverse(), key2);
    }

    @Test
    void compareAngle() {
        DeltaKey keyA = DeltaKey.between(BlockPos.ORIGIN, new BlockPos(1, 0, 0));
        DeltaKey keyB = DeltaKey.between(BlockPos.ORIGIN, new BlockPos(0, 0, 2));
        Assertions.assertTrue(keyA.getAngle() < keyB.getAngle());
        Assertions.assertTrue(keyA.compareTo(keyB) < 0);
    }

    @Test
    void compareAngleThenLength() {
        DeltaKey keyA = DeltaKey.between(BlockPos.ORIGIN, new BlockPos(1, 0, 0));
        DeltaKey keyB = DeltaKey.between(BlockPos.ORIGIN, new BlockPos(2, 0, 0));
        Assertions.assertEquals(keyA.getAngle(), keyB.getAngle());
        Assertions.assertTrue(keyA.getLength() < keyB.getLength());
        Assertions.assertTrue(keyA.compareTo(keyB) < 0);
    }

    @Test
    void compareAngleThenLengthThenBlockPos() {
        DeltaKey keyA = DeltaKey.between(BlockPos.ORIGIN, new BlockPos(1, -1, 0));
        DeltaKey keyB = DeltaKey.between(BlockPos.ORIGIN, new BlockPos(1, 1, 0));
        Assertions.assertEquals(keyA.getAngle(), keyB.getAngle());
        Assertions.assertEquals(keyA.getLength(), keyB.getLength());
        Assertions.assertTrue(keyA.getDelta().compareTo(keyB.getDelta()) < 0);
        Assertions.assertTrue(keyA.compareTo(keyB) < 0);
    }
}
