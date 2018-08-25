package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

class GraphBuilderTest {
    @Test
    void buildsSimpleGraph() {
        BlockPos posA = new BlockPos(0, 0, 0);
        BlockPos posB = new BlockPos(1, 0, 0);

        GraphBuilder builder = new GraphBuilder();
        GraphBuilder.NodeBuilder nodeA = builder.putNode(posA, 0);
        nodeA.putEdgeTo(posB);
        GraphBuilder.NodeBuilder nodeB = builder.putNode(posB, 0);
        nodeB.putEdgeTo(posA);
        Graph graph = builder.build();

        Graph.Edge a_b = new Graph.Edge(posB.subtract(posA), new Line(posA, posB), AttachmentUnit.UNITS_PER_BLOCK * 0, AttachmentUnit.UNITS_PER_BLOCK * 1);
        Graph.Edge b_a = new Graph.Edge(posA.subtract(posB), new Line(posB, posA), AttachmentUnit.UNITS_PER_BLOCK * 1, AttachmentUnit.UNITS_PER_BLOCK * 2);
        Graph.Node a = new Graph.Node(posA, Collections.singletonList(a_b), 0);
        Graph.Node b = new Graph.Node(posB, Collections.singletonList(b_a), 0);

        Assertions.assertEquals(graph.getNodes().keySet(), new HashSet<>(Arrays.asList(posA, posB)));
        Assertions.assertEquals(graph.getNodes().get(posA), a);
        Assertions.assertEquals(graph.getNodes().get(posB), b);
        Assertions.assertEquals(graph.getEdges(), Arrays.asList(a_b, b_a));
    }
}
