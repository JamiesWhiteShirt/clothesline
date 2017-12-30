package com.jamieswhiteshirt.clothesline.api.util;

import java.util.List;

public abstract class KeyRangeIndexLookup {
    private static class Inner extends KeyRangeIndexLookup {
        private final int key;
        private final KeyRangeIndexLookup left;
        private final KeyRangeIndexLookup right;

        private Inner(int key, KeyRangeIndexLookup left, KeyRangeIndexLookup right) {
            this.key = key;
            this.left = left;
            this.right = right;
        }

        @Override
        public int getMinIndex(int key) {
            if (key < this.key) {
                return left.getMinIndex(key);
            } else {
                return right.getMinIndex(key);
            }
        }
    }

    private static class Leaf extends KeyRangeIndexLookup {
        private final int value;
        private Leaf(int value) {
            this.value = value;
        }

        @Override
        public int getMinIndex(int key) {
            return value;
        }
    }

    private KeyRangeIndexLookup() { }

    public abstract int getMinIndex(int key);

    private static KeyRangeIndexLookup build(int baseIndex, List<Integer> keys) {
        if (keys.size() == 1) {
            return new Leaf(baseIndex);
        } else {
            int splitIndex = keys.size() / 2;
            return new Inner(
                    keys.get(splitIndex),
                    build(baseIndex, keys.subList(0, splitIndex)),
                    build(baseIndex + splitIndex, keys.subList(splitIndex, keys.size()))
            );
        }
    }

    public static KeyRangeIndexLookup build(List<Integer> keys) {
        return build(0, keys);
    }
}
