package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.api.EdgeComparator;
import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.api.Tree;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class NetworkStateBuilderTest {
    private NetworkStateBuilder createString(BlockPos... positions) {
        NetworkStateBuilder builder = NetworkStateBuilder.emptyRoot(0, positions[0]);
        for (int i = 1; i < positions.length; i++) {
            builder.addEdge(positions[i - 1], positions[i]);
        }
        return builder;
    }

    @Test
    void persistsEquivalence() {
        INetworkState a = NetworkTests.ab.state;
        NetworkStateBuilder builder = NetworkStateBuilder.fromAbsolute(a);
        INetworkState b = builder.toAbsolute();
        NetworkTests.assertNetworkStatesEquivalent(a, b);
    }

    @Test
    void canMakeAB() {
        NetworkStateBuilder builder = NetworkStateBuilder.emptyRoot(0, NetworkTests.posA);
        builder.addEdge(NetworkTests.posA, NetworkTests.posB);
        INetworkState state = builder.toAbsolute();
        NetworkTests.assertNetworkStatesEquivalent(state, NetworkTests.ab.state);
    }

    @Test
    void rerootABOnBEquivalentToBA() {
        BlockPos posA = new BlockPos(0, 0, 0);
        BlockPos posB = new BlockPos(1, 0, 0);
        NetworkStateBuilder ab = createString(posA, posB);
        NetworkStateBuilder ba = createString(posB, posA);
        ab.reroot(posB);
        Assertions.assertEquals(ba, ab);
    }

    @Test
    void splitsOffBasicRoot() {
        BlockPos posC = new BlockPos(-1, 0, 0);
        BlockPos posA = new BlockPos(0, 0, 0);
        BlockPos posB = new BlockPos(1, 0, 0);
        NetworkStateBuilder cab = createString(posC, posA, posB);
        NetworkStateBuilder ca = createString(posC, posA);
        NetworkStateBuilder ab = createString(posA, posB);
        NetworkStateBuilder.SplitResult splitResult = cab.splitRoot();
        Assertions.assertEquals(ca, splitResult.getState());
        Assertions.assertEquals(Collections.singletonList(ab), splitResult.getSubStates());
    }

    void assertStrictlyOrdered(List<BlockPos> vecs) {
        for (int i = 1; i < vecs.size(); i++) {
            for (int j = 0; j < vecs.size(); j++) {
                BlockPos a = vecs.get(i);
                BlockPos b = vecs.get(j);
                int c = EdgeComparator.getInstance().compare(a, b);
                if (i < j) {
                    Assertions.assertTrue(c < 0, "Expected " + a + " to be less than " + b);
                } else if (i > j) {
                    Assertions.assertTrue(c > 0, "Expected " + a + " to be greater than " + b);
                } else {
                    Assertions.assertEquals(0, c, "Expected " + a + " to equal " + b);
                }
            }
        }
    }

    @Test
    void treePreservesRootAndHasStrictlyOrderedEdges() {
        BlockPos origin = new BlockPos(0, 0, 0);
        NetworkStateBuilder builder = NetworkStateBuilder.emptyRoot(0, origin);
        Stream.of(new BlockPos(1, 0, 0), new BlockPos(0, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(-1, 1, 0), new BlockPos(0, 5, 5))
            .forEach(pos -> builder.addEdge(origin, pos));
        Tree tree = builder.toAbsolute().getTree();
        Assertions.assertEquals(origin, tree.getPos());
        List<Tree.Edge> edges = tree.getEdges();
        assertStrictlyOrdered(edges.stream().map(Tree.Edge::getDelta).collect(Collectors.toList()));
    }
}
