package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable data structure of a network as a tree. Values are absolute.
 */
public class AbsoluteTree {
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

        private void collectNodes(AbsoluteTree from, ArrayList<Node> nodes) {
            nodes.add(new Node(from, preMinOffset));
            tree.collectNodes(nodes);
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

    private void collectNodes(ArrayList<Node> nodes) {
        for (Edge edge : edges) {
            edge.collectNodes(this, nodes);
        }
        nodes.add(new Node(this, maxOffset));
    }

    public NodeLoop toNodeLoop() {
        if (!edges.isEmpty()) {
            ArrayList<Node> nodes = new ArrayList<>();
            collectNodes(nodes);
            return new NodeLoop(nodes.subList(0, nodes.size() - 1), maxOffset);
        } else {
            return NodeLoop.empty(this);
        }
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
}
