package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

class TreeTest {
    @Test
    void buildsSimpleGraph() {
        BlockPos posA = new BlockPos(0, 0, 0);
        BlockPos posB = new BlockPos(1, 0, 0);
        Tree tree = new Tree(
            posA, Collections.singletonList(new Tree.Edge(
                new EdgeKey(posA, posB), 0,
                new Tree(posB, Collections.emptyList(), Measurements.UNIT_LENGTH, Measurements.UNIT_LENGTH)
            )), 0, Measurements.UNIT_LENGTH * 2
        );

        Graph graph = tree.buildGraph();

        Graph.Edge a_b = new Graph.Edge(new EdgeKey(posA, posB), new Line(posA, posB), Measurements.UNIT_LENGTH * 0, Measurements.UNIT_LENGTH * 1);
        Graph.Edge b_a = new Graph.Edge(new EdgeKey(posB, posA), new Line(posB, posA), Measurements.UNIT_LENGTH * 1, Measurements.UNIT_LENGTH * 2);
        Graph.Node a = new Graph.Node(posA, Collections.singletonList(a_b));
        Graph.Node b = new Graph.Node(posB, Collections.singletonList(b_a));

        Assertions.assertEquals(graph.getNodes().keySet(), new HashSet<>(Arrays.asList(posA, posB)));
        Assertions.assertEquals(graph.getNodes().get(posA), a);
        Assertions.assertEquals(graph.getNodes().get(posB), b);
        Assertions.assertEquals(graph.getEdges(), Arrays.asList(a_b, b_a));
    }
}
