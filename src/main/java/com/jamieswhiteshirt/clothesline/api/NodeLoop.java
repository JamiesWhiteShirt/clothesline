package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

public class NodeLoop {
    private static float floorModAngle(float angle) {
        if (angle >= 0.0F) {
            return angle % 360.0F;
        } else {
            return 360.0F + (angle % 360.0F);
        }
    }

    private static float calculateAngleY(BlockPos from, BlockPos to) {
        return floorModAngle((float)Math.toDegrees(Math.atan2(to.getZ() - from.getZ(), to.getX() - from.getX())));
    }

    private static int calculateLength(BlockPos from, BlockPos to) {
        return (int)(100.0D * from.getDistance(to.getX(), to.getY(), to.getZ()));
    }

    public static NodeLoop buildInitial(BlockPos from, BlockPos to) {
        int length = calculateLength(from, to);
        float angleY = calculateAngleY(from, to);

        return new NodeLoop(Arrays.asList(
                new Node(from, 0, angleY),
                new Node(to, length, floorModAngle(angleY + 180.0F))
        ), length * 2);
    }

    public static NodeLoop merge(NodeLoop loopA, int indexA, NodeLoop loopB, int indexB) {
        Node nodeA = loopA.get(indexA);
        Node nodeB = loopB.get(indexB);

        ArrayList<Node> newNodes = new ArrayList<>(loopA.size() + loopB.size());

        newNodes.addAll(loopA.nodes.subList(0, indexA));
        newNodes.addAll(loopB.nodes.subList(indexB, loopB.nodes.size()).stream().map(
                node -> node.addOffset(nodeA.getOffset() - nodeB.getOffset())
        ).collect(Collectors.toList()));
        newNodes.addAll(loopB.nodes.subList(0, indexB).stream().map(
                node -> node.addOffset(nodeA.getOffset() + nodeB.getOffset())
        ).collect(Collectors.toList()));
        newNodes.addAll(loopA.nodes.subList(indexA, loopA.nodes.size()).stream().map(
                node -> node.addOffset(loopB.getLoopLength())
        ).collect(Collectors.toList()));

        return new NodeLoop(newNodes, loopA.getLoopLength() + loopB.getLoopLength());
    }

    private final List<Node> nodes;
    private final int loopLength;
    private final Node loopNode;

    /*private NodeLoop(BlockPos onlyPos) {
        this.nodes = Collections.emptyList();
        this.loopLength = 0;
        this.loopNode = new Node(onlyPos, 0, Float.NaN);
    }*/

    public NodeLoop(List<Node> nodes, int loopLength) {
        this.nodes = nodes;
        this.loopLength = loopLength;
        Node firstNode = nodes.get(0);
        this.loopNode = new Node(firstNode.getPos(), loopLength, firstNode.getAngleY());
    }

    public int size() {
        return nodes.size();
    }

    public Node get(int index) {
        return index != nodes.size() ? nodes.get(index) : loopNode;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public int getLoopLength() {
        return loopLength;
    }

    public Set<BlockPos> getPositions() {
        return nodes.stream().map(Node::getPos).collect(Collectors.toSet());
    }

    public int getMergeIndex(BlockPos fromPos, float angle) {
        for (int i = 0; i < nodes.size(); i++) {
            Node afterNode = nodes.get(i);
            if (afterNode.getPos().equals(fromPos)) {
                //return i;

                Node beforeNode = nodes.get(Math.floorMod(i - 1, nodes.size()));

                //The angle of the extension must between the angle of the two edges

                float beforeAngle = floorModAngle(beforeNode.getAngleY() + 180.0F);
                float afterAngle = afterNode.getAngleY();

                // The angle delta, the angle between the extension edge and the edge before it
                float angleDelta = floorModAngle(angle - beforeAngle);
                // The maximum angle delta, the angle between the edge before and after the extension edge
                float maxAngleDelta = floorModAngle(afterAngle - beforeAngle);

                if (angleDelta < maxAngleDelta || maxAngleDelta == 0.0D) {
                    return i;
                }
            }
        }
        return -1;
    }

    public NodeLoop mergeWith(NodeLoop other) {
        int extensionIndex = getMergeIndex(other.get(0).getPos(), other.get(0).getAngleY());
        if (extensionIndex != -1) {
            return merge(this, extensionIndex, other, 0);
        } else {
            return this;
        }
    }
}
