package com.jamieswhiteshirt.clothesline.common.util.rtree;

import java.util.function.Predicate;

public abstract class Node<T> {
    private static final int MAX_CHILDREN = 3;

    public static BoundingBox containingAll(Node... nodes) {
        BoundingBox initial = nodes[0].boundingBox;
        int minX = initial.minX;
        int minY = initial.minY;
        int minZ = initial.minZ;
        int maxX = initial.maxX;
        int maxY = initial.maxY;
        int maxZ = initial.maxZ;

        for (int i = 1; i < nodes.length; i++) {
            BoundingBox boundingBox = nodes[i].boundingBox;
            if (boundingBox.minX < minX) minX = boundingBox.minX;
            if (boundingBox.minY < minY) minY = boundingBox.minY;
            if (boundingBox.minZ < minZ) minZ = boundingBox.minZ;
            if (boundingBox.maxX > maxX) maxX = boundingBox.maxX;
            if (boundingBox.maxY > maxY) maxY = boundingBox.maxY;
            if (boundingBox.maxZ > maxZ) maxZ = boundingBox.maxZ;
        }

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static class Inner<T> extends Node<T> {
        private final Node[] children;

        private Inner(Node[] children) {
            super(containingAll(children));
            this.children = children;
        }

        @Override
        public boolean any(Point point, Predicate<T> predicate) {
            if (boundingBox.contains(point)) {
                for (Node<T> child : children) {
                    if (child.any(point, predicate)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static class Leaf<T> extends Node<T> {
        private final T value;

        private Leaf(BoundingBox boundingBox, T value) {
            super(boundingBox);
            this.value = value;
        }

        @Override
        public boolean any(Point point, Predicate<T> predicate) {
            return boundingBox.contains(point) && predicate.test(value);
        }
    }

    protected final BoundingBox boundingBox;

    protected Node(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public abstract boolean any(Point point, Predicate<T> predicate);
}
