package com.jamieswhiteshirt.clothesline.common.util;

import com.jamieswhiteshirt.clothesline.api.*;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PathBuilder {
    public final class NodeBuilder {
        private final BlockPos pos;
        private final List<Path.Edge> edges = new ArrayList<>();
        private final int baseRotation;

        private NodeBuilder(BlockPos pos, int baseRotation) {
            this.pos = pos;
            this.baseRotation = baseRotation;
        }

        private void putEdge(Path.Edge edge, int minIndex, int maxIndex) {
            if (minIndex != maxIndex) {
                int middleIndex = (minIndex + maxIndex) / 2;
                int comparison = DeltaComparator.getInstance().compare(edge.getDelta(), edges.get(middleIndex).getDelta());
                if (comparison < 0) {
                    putEdge(edge, minIndex, middleIndex);
                } else if (comparison > 0) {
                    putEdge(edge, middleIndex + 1, maxIndex);
                }
            } else {
                edges.add(minIndex, edge);
            }
        }

        public void putEdgeTo(BlockPos toPos, int length) {
            int minOffset = getMaxOffset();
            Path.Edge edge = new Path.Edge(toPos.subtract(this.pos), new Line(this.pos, toPos), minOffset, minOffset + length);
            allEdges.add(edge);
            putEdge(edge, 0, edges.size());
        }
    }

    private final Map<BlockPos, NodeBuilder> nodes = new HashMap<>();
    private final List<Path.Edge> allEdges = new ArrayList<>();

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

    public Path build() {
        Map<BlockPos, Path.Node> nodes = this.nodes.values().stream()
            .map(nodeBuilder -> new Path.Node(nodeBuilder.pos, nodeBuilder.edges, nodeBuilder.baseRotation))
            .collect(Collectors.toMap(Path.Node::getPos, Function.identity()));
        return new Path(nodes, new ArrayList<>(allEdges));
    }

    private static PathBuilder.NodeBuilder buildPath(PathBuilder pathBuilder, Tree tree) {
        PathBuilder.NodeBuilder nodeBuilder = pathBuilder.putNode(tree.getPos(), tree.getBaseRotation());
        for (Tree.Edge edge : tree.getEdges()) {
            nodeBuilder.putEdgeTo(edge.getTree().getPos(), edge.getLength());
            PathBuilder.NodeBuilder childNodeBuilder = buildPath(pathBuilder, edge.getTree());
            childNodeBuilder.putEdgeTo(tree.getPos(), edge.getLength());
        }
        return nodeBuilder;
    }

    public static Path buildPath(Tree tree) {
        PathBuilder builder = new PathBuilder();
        buildPath(builder, tree);
        return builder.build();
    }
}
