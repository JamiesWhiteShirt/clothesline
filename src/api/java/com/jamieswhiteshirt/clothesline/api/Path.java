package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.*;

/**
 * A path through a graph of nodes and edges representing the traversal of a clothesline network.
 */
public final class Path {
    public static final class Node {
        private final BlockPos pos;
        private final List<Edge> edges;
        private final int baseRotation;

        public Node(BlockPos pos, List<Edge> edges, int baseRotation) {
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

        private int flooredEdgeIndex(Vec3i delta, int left, int right) {
            while (left < right) {
                int mid = (left + right) / 2;
                int comparison = DeltaComparator.getInstance().compare(edges.get(mid).delta, delta);
                if (comparison > 0) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            return left;
        }

        private int flooredEdgeIndex(Vec3i delta) {
            return flooredEdgeIndex(delta, 0, edges.size());
        }

        public int getOffsetForDelta(Vec3i delta) {
            return edges.get(flooredEdgeIndex(delta) % edges.size()).fromOffset;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return baseRotation == node.baseRotation &&
                Objects.equals(pos, node.pos) &&
                Objects.equals(edges, node.edges);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, edges, baseRotation);
        }
    }

    public static final class Edge {
        private final BlockPos delta;
        private final Line line;
        private final int fromOffset;
        private final int toOffset;

        public Edge(BlockPos delta, Line line, int fromOffset, int toOffset) {
            this.delta = delta;
            this.line = line;
            this.fromOffset = fromOffset;
            this.toOffset = toOffset;
        }

        public BlockPos getDelta() {
            return delta;
        }

        public Line getLine() {
            return line;
        }

        public int getFromOffset() {
            return fromOffset;
        }

        public int getToOffset() {
            return toOffset;
        }

        public int getLength() {
            return toOffset - fromOffset;
        }

        public Vec3d getPositionForOffset(int offset) {
            double scalar = (double)(offset - getFromOffset()) / (getToOffset() - getFromOffset());
            return line.getPosition(scalar);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return fromOffset == edge.fromOffset &&
                toOffset == edge.toOffset &&
                Objects.equals(line, edge.line);
        }

        @Override
        public int hashCode() {
            return Objects.hash(line, fromOffset, toOffset);
        }
    }

    private final Map<BlockPos, Node> nodes;
    private final List<Edge> edges;

    public Path(Map<BlockPos, Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public int getLength() {
        if (edges.isEmpty()) return 0;

        return edges.get(edges.size() - 1).toOffset;
    }

    public Map<BlockPos, Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    private Edge getEdgeForPosition(int offset, int fromIndex, int toIndex) {
        if (fromIndex != toIndex) {
            int middleIndex = (fromIndex + toIndex) / 2;
            Edge edge = edges.get(middleIndex);
            if (offset < edge.fromOffset) {
                return getEdgeForPosition(offset, fromIndex, middleIndex);
            } else if (offset >= edge.toOffset) {
                return getEdgeForPosition(offset, middleIndex + 1, toIndex);
            } else {
                return edge;
            }
        } else {
            return edges.get(fromIndex);
        }
    }

    public Edge getEdgeForPosition(int offset) {
        return getEdgeForPosition(offset, 0, edges.size());
    }

    public Vec3d getPositionForOffset(int offset) {
        return getEdgeForPosition(offset).getPositionForOffset(offset);
    }

    public boolean isEmpty() {
        return edges.isEmpty();
    }
}
