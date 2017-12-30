package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;
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

        private NetworkGraph.Edge getGraphEdgeForOffset(AbsoluteTree fromTree, int offset) {
            if (offset < getPreMaxOffset()) {
                return new NetworkGraph.Edge(key, fromTree.pos, tree.pos, getPreMinOffset(), getPreMaxOffset());
            } else if (offset < getPostMinOffset()) {
                return tree.getGraphEdgeForOffset(offset);
            } else {
                return new NetworkGraph.Edge(key.reverse(fromTree.pos), tree.pos, fromTree.pos, getPostMinOffset(), getPostMaxOffset());
            }
        }
    }

    public static AbsoluteTree empty(BlockPos pos, int offset) {
        return new AbsoluteTree(pos, Collections.emptyList(), offset, offset);
    }

    private int findEdgeKeyIndex(EdgeKey key, int minIndex, int maxIndex) {
        if (minIndex != maxIndex) {
            int middleIndex = (minIndex + maxIndex) / 2;
            int comparison = key.compareTo(edges.get(middleIndex).getKey());
            if (comparison < 0) {
                return findEdgeKeyIndex(key, minIndex, middleIndex);
            } else if (comparison > 0) {
                return findEdgeKeyIndex(key, middleIndex + 1, maxIndex);
            } else {
                return middleIndex;
            }
        } else {
            return minIndex;
        }
    }

    public int findEdgeKeyIndex(EdgeKey key) {
        return findEdgeKeyIndex(key, 0, edges.size());
    }

    public int findCornerOffset(EdgeKey key) {
        int edgeIndex = findEdgeKeyIndex(key);
        if (edgeIndex != edges.size()) {
            return edges.get(edgeIndex).getPreMinOffset();
        } else {
            return maxOffset;
        }
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

    private void buildPositionLookup(HashMap<BlockPos, AbsoluteTree> result) {
        result.put(pos, this);
        for (Edge edge : edges) {
            edge.getTree().buildPositionLookup(result);
        }
    }

    public Map<BlockPos, AbsoluteTree> createPositionLookup() {
        HashMap<BlockPos, AbsoluteTree> result = new HashMap<>();
        buildPositionLookup(result);
        return result;
    }

    private NetworkGraph.Node buildGraph(NetworkGraph graph) {
        NetworkGraph.Node node = graph.putNode(pos);
        for (Edge edge : edges) {
            node.putEdge(edge.key, edge.tree.pos);
            NetworkGraph.Node childNode = edge.tree.buildGraph(graph);
            childNode.putEdge(edge.key.reverse(edge.tree.pos), pos);
        }
        return node;
    }

    public NetworkGraph toGraph() {
        NetworkGraph graph = new NetworkGraph();
        buildGraph(graph);
        return graph;
    }

    public NetworkGraph.Edge getGraphEdgeForOffset(int offset) {
        if (offset >= minOffset) {
            for (Edge edge : edges) {
                if (offset < edge.getPostMaxOffset()) {
                    return edge.getGraphEdgeForOffset(this, offset);
                }
            }
        }
        throw new IllegalArgumentException("Offset is not in AbsoluteTree");
    }

    public Vec3d getPositionForOffset(int offset) {
        return getGraphEdgeForOffset(offset).getPositionForOffset(offset);
    }
}
