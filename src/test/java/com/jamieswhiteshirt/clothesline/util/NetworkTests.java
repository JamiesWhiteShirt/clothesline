package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.common.impl.Network;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;
import java.util.UUID;

class NetworkTests {
    static BlockPos posA = new BlockPos(0, 0, 0);
    static BlockPos posB = new BlockPos(1, 0, 0);

    static class NetworkDataSet {
        final BlockPos from;
        final BlockPos to;
        final Tree tree;
        final MutableSortedIntMap<ItemStack> attachments;
        final NetworkState state;
        final PersistentNetwork persistentNetwork;
        final Network network;

        NetworkDataSet(BlockPos from, BlockPos to) {
            this.from = from;
            this.to = to;
            this.tree = new Tree(
                from,
                Collections.singletonList(
                    new Tree.Edge(new EdgeKey(from, to), 0, Tree.empty(to, Measurements.UNIT_LENGTH))
                ),
                0, Measurements.UNIT_LENGTH * 2
            );
            this.attachments = MutableSortedIntMap.empty(Measurements.UNIT_LENGTH * 2);
            this.state = new NetworkState(0, 0, 0, 0, tree, attachments);
            this.persistentNetwork = new PersistentNetwork(new UUID(0, 0), state);
            this.network = new Network(0, persistentNetwork);
        }
    }

    static NetworkDataSet ab = new NetworkDataSet(posA, posB);

    static void assertNetworksEquivalent(Network expected, Network actual) {
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getUuid(), actual.getUuid());
        assertNetworkStatesEquivalent(expected.getState(), actual.getState());
    }

    static void assertNetworkStatesEquivalent(NetworkState expected, NetworkState actual) {
        Assertions.assertEquals(expected.getShift(), actual.getShift());
        Assertions.assertEquals(expected.getMomentum(), actual.getMomentum());
        Assertions.assertEquals(expected.getTree(), actual.getTree());
        Assertions.assertEquals(expected.getAttachments(), actual.getAttachments());
    }
}