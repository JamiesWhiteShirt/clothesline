package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.DeltaKey;
import com.jamieswhiteshirt.clothesline.api.Tree;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Minimal immutable data structure for a BlockPos tree for serialization purposes.
 * Children are expected to be ordered by {@link DeltaKey#compareTo(DeltaKey)} of their
 * their {@link #getPos()} with this {@link #getPos()} as the origin.
 */
public final class BasicTree {
    public static final class Edge {
        private final int length;
        private final BasicTree tree;

        public Edge(int length, BasicTree tree) {
            this.length = length;
            this.tree = tree;
        }

        public int getLength() {
            return length;
        }

        public BasicTree getTree() {
            return tree;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return length == edge.length &&
                Objects.equals(tree, edge.tree);
        }

        @Override
        public int hashCode() {
            return Objects.hash(length, tree);
        }

        @Override
        public String toString() {
            return "Edge{" +
                "length=" + length +
                ", tree=" + tree +
                '}';
        }

        public static Edge fromAbsolute(Tree.Edge edge) {
            return new Edge(
                edge.getLength(),
                BasicTree.fromAbsolute(edge.getTree())
            );
        }
    }

    public static BasicTree fromAbsolute(Tree tree) {
        return new BasicTree(
            tree.getPos(),
            tree.getEdges().stream().map(Edge::fromAbsolute).collect(Collectors.toList()),
            tree.getBaseRotation()
        );
    }

    private final BlockPos pos;
    private final List<Edge> edges;
    private final int baseRotation;

    public BasicTree(BlockPos pos, List<Edge> edges, int baseRotation) {
        this.pos = pos;
        this.edges = edges;
        this.baseRotation = baseRotation;
    }

    public BlockPos getPos() {
        return pos;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int getBaseRotation() {
        return baseRotation;
    }

    private Tree toAbsolute(int fromOffset) {
        int toOffset = fromOffset;
        ArrayList<Tree.Edge> edgesOut = new ArrayList<>(edges.size());
        for (Edge edge : edges) {
            DeltaKey key = DeltaKey.between(pos, edge.tree.pos);
            Tree treeOut = edge.tree.toAbsolute(toOffset + edge.length);
            Tree.Edge edgeOut = new Tree.Edge(key, edge.length, toOffset, treeOut);
            edgesOut.add(edgeOut);
            toOffset = edgeOut.getPostMaxOffset();
        }
        return new Tree(pos, edgesOut, fromOffset, toOffset, baseRotation);
    }

    public Tree toAbsolute() {
        return toAbsolute(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicTree basicTree = (BasicTree) o;
        return baseRotation == basicTree.baseRotation &&
            Objects.equals(pos, basicTree.pos) &&
            Objects.equals(edges, basicTree.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, edges);
    }

    @Override
    public String toString() {
        return "BasicTree{" +
            "pos=" + pos +
            ", edges=" + edges +
            ", baseRotation" + baseRotation +
            '}';
    }
}
