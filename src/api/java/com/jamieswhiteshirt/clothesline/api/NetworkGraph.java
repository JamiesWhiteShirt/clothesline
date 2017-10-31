package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.*;

public class NetworkGraph {
    public class Node {
        private final BlockPos key;
        private final List<Edge> edges = new ArrayList<>();

        private Node(BlockPos key) {
            this.key = key;
        }

        public BlockPos getKey() {
            return key;
        }

        private void putEdge(Edge edge, int minIndex, int maxIndex) {
            if (minIndex != maxIndex) {
                int middleIndex = (minIndex + maxIndex) / 2;
                int comparison = edge.getKey().compareTo(edges.get(middleIndex).getKey());
                if (comparison < 0) {
                    putEdge(edge, minIndex, middleIndex);
                } else if (comparison > 0) {
                    putEdge(edge, middleIndex + 1, maxIndex);
                }
            } else {
                edges.add(minIndex, edge);
            }
        }

        public void putEdge(EdgeKey key, BlockPos toKey) {
            int minOffset = getMaxOffset();
            Edge edge = new Edge(key, this.key, toKey, minOffset, minOffset + key.getLength());
            allEdges.add(edge);
            putEdge(edge, 0, edges.size());
        }

        private int floorEdgeIndex(EdgeKey key, int minIndex, int maxIndex) {
            if (minIndex != maxIndex) {
                int middleIndex = (minIndex + maxIndex) / 2;
                int comparison = key.compareTo(edges.get(middleIndex).getKey());
                if (comparison < 0) {
                    return floorEdgeIndex(key, minIndex, middleIndex);
                } else if (comparison > 0) {
                    return floorEdgeIndex(key, middleIndex + 1, maxIndex);
                } else {
                    return middleIndex;
                }
            } else {
                return minIndex;
            }
        }

        private int floorEdgeIndex(EdgeKey key) {
            return floorEdgeIndex(key, 0, edges.size());
        }

        public int getCornerOffset(EdgeKey key) {
            return edges.get(floorEdgeIndex(key) % edges.size()).fromOffset;
        }
    }

    public static class Edge {
        private final EdgeKey key;
        private final BlockPos fromKey;
        private final BlockPos toKey;
        private final int fromOffset;
        private final int toOffset;

        public Edge(EdgeKey key, BlockPos fromKey, BlockPos toKey, int fromOffset, int toOffset) {
            this.key = key;
            this.fromKey = fromKey;
            this.toKey = toKey;
            this.fromOffset = fromOffset;
            this.toOffset = toOffset;
        }

        public EdgeKey getKey() {
            return key;
        }

        public BlockPos getFromKey() {
            return fromKey;
        }

        public BlockPos getToKey() {
            return toKey;
        }

        public int getFromOffset() {
            return fromOffset;
        }

        public int getToOffset() {
            return toOffset;
        }
    }

    private final Map<BlockPos, Node> nodes = new HashMap<>();
    private final List<Edge> allEdges = new ArrayList<>();

    public int getMaxOffset() {
        if (!allEdges.isEmpty()) {
            return allEdges.get(allEdges.size() - 1).toOffset;
        } else {
            return 0;
        }
    }

    public Node putNode(BlockPos key) {
        Node node = new Node(key);
        nodes.put(key, node);
        return node;
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public List<Edge> getAllEdges() {
        return allEdges;
    }
}
