package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * Immutable data structure of a network as a tree. Values are absolute.
 */
public final class Tree {
    public static final class Edge {
        private final BlockPos delta;
        private final int length;
        private final int preMinOffset;
        private final Tree tree;

        public Edge(BlockPos delta, int length, int preMinOffset, Tree tree) {
            this.delta = delta;
            this.length = length;
            this.preMinOffset = preMinOffset;
            this.tree = tree;
        }

        public BlockPos getDelta() {
            return delta;
        }

        public int getLength() {
            return length;
        }

        public Tree getTree() {
            return tree;
        }

        public int getPreMinOffset() {
            return preMinOffset;
        }

        public int getPreMaxOffset() {
            return preMinOffset + length;
        }

        public int getPostMinOffset() {
            return tree.maxOffset;
        }

        public int getPostMaxOffset() {
            return tree.maxOffset + length;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return length == edge.length &&
                preMinOffset == edge.preMinOffset &&
                Objects.equals(delta, edge.delta) &&
                Objects.equals(tree, edge.tree);
        }

        @Override
        public int hashCode() {
            return Objects.hash(delta, length, preMinOffset, tree);
        }

        @Override
        public String toString() {
            return "Edge{" +
                "delta=" + delta +
                ", length=" + length +
                ", preMinOffset=" + preMinOffset +
                ", tree=" + tree +
                '}';
        }
    }

    public static Tree empty(BlockPos pos, int offset, int baseRotation) {
        return new Tree(pos, Collections.emptyList(), offset, offset, baseRotation);
    }

    private final BlockPos pos;
    private final List<Edge> edges;
    private final int minOffset;
    private final int maxOffset;
    private final int baseRotation;

    public Tree(BlockPos pos, List<Edge> edges, int minOffset, int maxOffset, int baseRotation) {
        this.pos = pos;
        this.edges = edges;
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
        this.baseRotation = baseRotation;
    }

    public BlockPos getPos() {
        return pos;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int getLoopLength() {
        return maxOffset - minOffset;
    }

    public int getMinOffset() {
        return minOffset;
    }

    public int getMaxOffset() {
        return maxOffset;
    }

    public int getBaseRotation() {
        return baseRotation;
    }

    public boolean isEmpty() {
        return edges.isEmpty();
    }

    private GraphBuilder.NodeBuilder buildGraph(GraphBuilder graphBuilder) {
        GraphBuilder.NodeBuilder nodeBuilder = graphBuilder.putNode(pos, baseRotation);
        for (Edge edge : edges) {
            nodeBuilder.putEdgeTo(edge.tree.pos);
            GraphBuilder.NodeBuilder childNodeBuilder = edge.tree.buildGraph(graphBuilder);
            childNodeBuilder.putEdgeTo(pos);
        }
        return nodeBuilder;
    }

    public Graph buildGraph() {
        GraphBuilder builder = new GraphBuilder();
        buildGraph(builder);
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tree that = (Tree) o;
        return minOffset == that.minOffset &&
            maxOffset == that.maxOffset &&
            baseRotation == that.baseRotation &&
            Objects.equals(pos, that.pos) &&
            Objects.equals(edges, that.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minOffset, maxOffset, pos, edges);
    }

    @Override
    public String toString() {
        return "Tree{" +
            "pos=" + pos +
            ", edges=" + edges +
            ", minOffset=" + minOffset +
            ", maxOffset=" + maxOffset +
            ", baseRotation=" + baseRotation +
            '}';
    }
}
