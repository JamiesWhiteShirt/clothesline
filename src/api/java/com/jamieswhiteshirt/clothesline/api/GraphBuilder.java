package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class GraphBuilder {
    public final class NodeBuilder {
        private final BlockPos pos;
        private final List<Graph.Edge> edges = new ArrayList<>();
        private final int baseRotation;

        private NodeBuilder(BlockPos pos, int baseRotation) {
            this.pos = pos;
            this.baseRotation = baseRotation;
        }

        private void putEdge(Graph.Edge edge, int minIndex, int maxIndex) {
            if (minIndex != maxIndex) {
                int middleIndex = (minIndex + maxIndex) / 2;
                int comparison = EdgeComparator.getInstance().compare(edge.getDelta(), edges.get(middleIndex).getDelta());
                if (comparison < 0) {
                    putEdge(edge, minIndex, middleIndex);
                } else if (comparison > 0) {
                    putEdge(edge, middleIndex + 1, maxIndex);
                }
            } else {
                edges.add(minIndex, edge);
            }
        }

        public void putEdgeTo(BlockPos toPos) {
            int minOffset = getMaxOffset();
            int length = Measurements.calculateDistance(this.pos, toPos);
            Graph.Edge edge = new Graph.Edge(toPos.subtract(this.pos), new Line(this.pos, toPos), minOffset, minOffset + length);
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

    public NodeBuilder putNode(BlockPos pos, int baseRotation) {
        NodeBuilder nodeBuilder = new NodeBuilder(pos, baseRotation);
        nodes.put(pos, nodeBuilder);
        return nodeBuilder;
    }

    public Graph build() {
        Map<BlockPos, Graph.Node> nodes = this.nodes.values().stream()
            .map(nodeBuilder -> new Graph.Node(nodeBuilder.pos, nodeBuilder.edges, nodeBuilder.baseRotation))
            .collect(Collectors.toMap(Graph.Node::getPos, Function.identity()));
        return new Graph(nodes, new ArrayList<>(allEdges));
    }
}
