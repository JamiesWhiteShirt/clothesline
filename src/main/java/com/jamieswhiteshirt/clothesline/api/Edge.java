package com.jamieswhiteshirt.clothesline.api;

public class Edge {
    private final Node a;
    private final Node b;

    public Edge(Node a, Node b) {
        this.a = a;
        this.b = b;
    }

    public Node getA() {
        return a;
    }

    public Node getB() {
        return b;
    }
}
