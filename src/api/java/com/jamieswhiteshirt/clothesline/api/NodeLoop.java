package com.jamieswhiteshirt.clothesline.api;

import java.util.*;

public class NodeLoop {
    public static NodeLoop empty(AbsoluteTree tree) {
        return new NodeLoop(tree);
    }

    private final List<Node> nodes;
    private final int loopLength;
    private final Node loopNode;

    private NodeLoop(AbsoluteTree onlyTree) {
        this.nodes = Collections.emptyList();
        this.loopLength = 0;
        this.loopNode = new Node(onlyTree, 0);
    }

    public NodeLoop(List<Node> nodes, int loopLength) {
        this.nodes = nodes;
        this.loopLength = loopLength;
        Node firstNode = nodes.get(0);
        this.loopNode = new Node(firstNode.getTree(), loopLength);
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int size() {
        return nodes.size();
    }

    public Node get(int index) {
        return index != nodes.size() ? nodes.get(index) : loopNode;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public int getLoopLength() {
        return loopLength;
    }
}
