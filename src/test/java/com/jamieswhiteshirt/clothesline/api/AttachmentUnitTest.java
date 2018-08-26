package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AttachmentUnitTest {
    @Test
    void calculateDistanceIsCommutative() {
        BlockPos[] positions = new BlockPos[] {
            new BlockPos(0, 0, 0),
            new BlockPos(100, 0, 0),
            new BlockPos(0, 200, 0),
            new BlockPos(0, 0, 300)
        };
        for (int i = 0; i < positions.length; i++) {
            BlockPos posA = positions[i];
            for (int j = i + 1; j < positions.length; j++) {
                BlockPos posB = positions[j];
                Assertions.assertEquals(AttachmentUnit.lengthBetween(posA, posB), AttachmentUnit.lengthBetween(posB, posA));
            }
        }
    }
}
