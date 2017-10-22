package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.api.AbsoluteTree;

public class Node {
    private final AbsoluteTree tree;
    private final int offset;

    public Node(AbsoluteTree tree, int offset) {
        this.tree = tree;
        this.offset = offset;
    }

    public AbsoluteTree getTree() {
        return tree;
    }

    public int getOffset() {
        return offset;
    }
}
