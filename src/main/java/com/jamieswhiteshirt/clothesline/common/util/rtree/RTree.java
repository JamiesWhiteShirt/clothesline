package com.jamieswhiteshirt.clothesline.common.util.rtree;


import javax.annotation.Nullable;
import java.util.function.Predicate;

public class RTree<T> {
    @Nullable
    private Node<T> root;

    public RTree() {
        root = null;
    }

    public boolean any(Point point, Predicate<T> predicate) {
        return root != null && root.any(point, predicate);
    }
}
