package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class GraphBuilder {
    public final class NodeBuilder {
        private final BlockPos key;
        private final List<Graph.Edge> edges = new ArrayList<>();

        private NodeBuilder(BlockPos key) {
            this.key = key;
        }

        private void putEdge(Graph.Edge edge, int minIndex, int maxIndex) {
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

        public void putEdge(DeltaKey key, BlockPos toKey) {
            int minOffset = getMaxOffset();
            Graph.Edge edge = new Graph.Edge(key, new Line(this.key, toKey), minOffset, minOffset + key.getLength());
            allEdges.add(edge);
            putEdge(edge, 0, edges.size());
        }
    }

    private final Map<BlockPos, NodeBuilder> nodes = new HashMap<>();
    private final List<Graph.Edge> allEdges = new ArrayList<>();

    public int getMaxOffset() {
        if (!allEdges.isEmpty()) {
            return allEdges.get(allEdges.size() - 1).getToOffset();
        } else {
            return 0;
        }
    }

    public NodeBuilder putNode(BlockPos key) {
        NodeBuilder nodeBuilder = new NodeBuilder(key);
        nodes.put(key, nodeBuilder);
        return nodeBuilder;
    }

    public Graph build() {
        Map<BlockPos, Graph.Node> nodes = this.nodes.values().stream()
            .map(nodeBuilder -> new Graph.Node(nodeBuilder.key, nodeBuilder.edges))
            .collect(Collectors.toMap(Graph.Node::getKey, Function.identity()));
        return new Graph(nodes, new ArrayList<>(allEdges));
    }
}
