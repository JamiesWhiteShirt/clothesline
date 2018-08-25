package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.common.impl.Network;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkState;
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
        final INetworkState state;
        final PersistentNetwork persistentNetwork;
        final Network network;

        NetworkDataSet(BlockPos from, BlockPos to) {
            this.from = from;
            this.to = to;
            this.tree = new Tree(
                from,
                Collections.singletonList(
                    new Tree.Edge(to.subtract(from), AttachmentUnit.lengthBetween(from, to), 0, Tree.empty(to, AttachmentUnit.UNITS_PER_BLOCK, 0))
                ),
                0, AttachmentUnit.UNITS_PER_BLOCK * 2, 0
            );
            this.attachments = MutableSortedIntMap.empty(AttachmentUnit.UNITS_PER_BLOCK * 2);
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

    static void assertNetworkStatesEquivalent(INetworkState expected, INetworkState actual) {
        Assertions.assertEquals(expected.getShift(), actual.getShift());
        Assertions.assertEquals(expected.getMomentum(), actual.getMomentum());
        Assertions.assertEquals(expected.getTree(), actual.getTree());
        Assertions.assertEquals(expected.getAttachments(), actual.getAttachments());
    }
}
