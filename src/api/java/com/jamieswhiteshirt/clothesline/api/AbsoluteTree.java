package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Immutable data structure of a network as a tree. Values are absolute.
 */
public final class AbsoluteTree {
    public static class Edge {
        private final EdgeKey key;
        private final int preMinOffset;
        private final AbsoluteTree tree;

        public Edge(EdgeKey key, int preMinOffset, AbsoluteTree tree) {
            this.key = key;
            this.preMinOffset = preMinOffset;
            this.tree = tree;
        }

        public EdgeKey getKey() {
            return key;
        }

        public AbsoluteTree getTree() {
            return tree;
        }

        public int getPreMinOffset() {
            return preMinOffset;
        }

        public int getPreMaxOffset() {
            return preMinOffset + key.getLength();
        }

        public int getPostMinOffset() {
            return tree.maxOffset;
        }

        public int getPostMaxOffset() {
            return tree.maxOffset + key.getLength();
        }
    }

    public static AbsoluteTree empty(BlockPos pos, int offset) {
        return new AbsoluteTree(pos, Collections.emptyList(), offset, offset);
    }

    private final int minOffset;
    private final int maxOffset;
    private final BlockPos pos;
    private final List<Edge> edges;

    public AbsoluteTree(BlockPos pos, List<Edge> edges, int minOffset, int maxOffset) {
        this.pos = pos;
        this.edges = edges;
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
    }

    public int getMinOffset() {
        return minOffset;
    }

    public int getMaxOffset() {
        return maxOffset;
    }

    public int getLoopLength() {
        return maxOffset - minOffset;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<AbsoluteTree> getChildren() {
        return edges.stream().map(edge -> edge.tree).collect(Collectors.toList());
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean isEmpty() {
        return edges.isEmpty();
    }

    private GraphBuilder.NodeBuilder buildGraph(GraphBuilder graphBuilder) {
        GraphBuilder.NodeBuilder nodeBuilder = graphBuilder.putNode(pos);
        for (Edge edge : edges) {
            nodeBuilder.putEdge(edge.key, edge.tree.pos);
            GraphBuilder.NodeBuilder childNodeBuilder = edge.tree.buildGraph(graphBuilder);
            childNodeBuilder.putEdge(edge.key.reverse(pos), pos);
        }
        return nodeBuilder;
    }

    public Graph buildGraph() {
        GraphBuilder builder = new GraphBuilder();
        buildGraph(builder);
        return builder.build();
    }
}
