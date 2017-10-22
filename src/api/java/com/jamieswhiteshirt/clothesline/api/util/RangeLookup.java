package com.jamieswhiteshirt.clothesline.api.util;

import java.util.List;

public abstract class RangeLookup {
    private static class Inner extends RangeLookup {
        private final int offset;
        private final RangeLookup left;
        private final RangeLookup right;

        private Inner(int offset, RangeLookup left, RangeLookup right) {
            this.offset = offset;
            this.left = left;
            this.right = right;
        }

        @Override
        public int getMinIndex(int offset) {
            if (offset < this.offset) {
                return left.getMinIndex(offset);
            } else {
                return right.getMinIndex(offset);
            }
        }
    }

    private static class Leaf extends RangeLookup {
        private final int value;
        private Leaf(int value) {
            this.value = value;
        }

        @Override
        public int getMinIndex(int offset) {
            return value;
        }
    }

    public abstract int getMinIndex(int offset);

    public static RangeLookup build(int baseIndex, List<Integer> offsets) {
        if (offsets.size() == 1) {
            return new Leaf(baseIndex);
        } else {
            int midOffset = offsets.get(offsets.size() - 1) / 2;
            int splitIndex = getSplitIndexByOffset(offsets, midOffset);
            return new Inner(
                    offsets.get(splitIndex),
                    build(baseIndex, offsets.subList(0, splitIndex)),
                    build(baseIndex + splitIndex, offsets.subList(splitIndex, offsets.size()))
            );
        }
    }

    private static int getSplitIndexByOffset(List<Integer> offsets, int offset) {
        if (offsets.size() == 2) {
            return 1;
        } else {
            int midIndex = offsets.size() / 2;
            int midOffset = offsets.get(midIndex);
            if (offset < midOffset) {
                return getSplitIndexByOffset(offsets.subList(0, midIndex + 1), offset);
            } else {
                return midIndex + getSplitIndexByOffset(offsets.subList(midIndex, offsets.size()), offset);
            }
        }
    }
}
