package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.api.AttachmentUnit;
import com.jamieswhiteshirt.clothesline.api.Line;
import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.api.Tree;
import com.jamieswhiteshirt.clothesline.common.util.PathBuilder;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

class PathBuilderTest {
    @Test
    void buildsSimplePath() {
        BlockPos posA = new BlockPos(0, 0, 0);
        BlockPos posB = new BlockPos(1, 0, 0);

        PathBuilder builder = new PathBuilder();
        PathBuilder.NodeBuilder nodeA = builder.putNode(posA, 0);
        nodeA.putEdgeTo(posB, AttachmentUnit.lengthBetween(posA, posB));
        PathBuilder.NodeBuilder nodeB = builder.putNode(posB, 0);
        nodeB.putEdgeTo(posA, AttachmentUnit.lengthBetween(posB, posA));
        Path path = builder.build();

        Path.Edge a_b = new Path.Edge(posB.subtract(posA), new Line(posA, posB), AttachmentUnit.UNITS_PER_BLOCK * 0, AttachmentUnit.UNITS_PER_BLOCK * 1);
        Path.Edge b_a = new Path.Edge(posA.subtract(posB), new Line(posB, posA), AttachmentUnit.UNITS_PER_BLOCK * 1, AttachmentUnit.UNITS_PER_BLOCK * 2);
        Path.Node a = new Path.Node(posA, Collections.singletonList(a_b), 0);
        Path.Node b = new Path.Node(posB, Collections.singletonList(b_a), 0);

        Assertions.assertEquals(path.getNodes().keySet(), new HashSet<>(Arrays.asList(posA, posB)));
        Assertions.assertEquals(path.getNodes().get(posA), a);
        Assertions.assertEquals(path.getNodes().get(posB), b);
        Assertions.assertEquals(path.getEdges(), Arrays.asList(a_b, b_a));
    }

    @Test
    void buildsSimplePathFromTree() {
        BlockPos posA = new BlockPos(0, 0, 0);
        BlockPos posB = new BlockPos(1, 0, 0);
        Tree tree = new Tree(
            posA, Collections.singletonList(new Tree.Edge(
                posB.subtract(posA),
                AttachmentUnit.lengthBetween(posA, posB),
                0,
                Tree.empty(posB, AttachmentUnit.lengthBetween(posA, posB), 0)
            )), 0, AttachmentUnit.lengthBetween(posA, posB) * 2, 0
        );

        Path path = PathBuilder.buildPath(tree);

        Path.Edge a_b = new Path.Edge(posB.subtract(posA), new Line(posA, posB), AttachmentUnit.UNITS_PER_BLOCK * 0, AttachmentUnit.UNITS_PER_BLOCK * 1);
        Path.Edge b_a = new Path.Edge(posA.subtract(posB), new Line(posB, posA), AttachmentUnit.UNITS_PER_BLOCK * 1, AttachmentUnit.UNITS_PER_BLOCK * 2);
        Path.Node a = new Path.Node(posA, Collections.singletonList(a_b), 0);
        Path.Node b = new Path.Node(posB, Collections.singletonList(b_a), 0);

        Assertions.assertEquals(path.getNodes().keySet(), new HashSet<>(Arrays.asList(posA, posB)));
        Assertions.assertEquals(path.getNodes().get(posA), a);
        Assertions.assertEquals(path.getNodes().get(posB), b);
        Assertions.assertEquals(path.getEdges(), Arrays.asList(a_b, b_a));
    }
}
