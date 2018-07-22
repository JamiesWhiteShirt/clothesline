package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.common.impl.Network;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;
import java.util.UUID;

class Networks {
    static BlockPos posA = new BlockPos(0, 0, 0);
    static BlockPos posB = new BlockPos(1, 0, 0);

    static Tree treeAB = new Tree(
        posA,
        Collections.singletonList(
            new Tree.Edge(new EdgeKey(posA, posB), 0, Tree.empty(posB, Measurements.UNIT_LENGTH))
        ),
        0, Measurements.UNIT_LENGTH * 2
    );
    static MutableSortedIntMap<ItemStack> attachmentsAB = MutableSortedIntMap.empty(Measurements.UNIT_LENGTH * 2);
    static NetworkState networkStateAB = new NetworkState(0, 0, 0, 0, treeAB, attachmentsAB);
    static PersistentNetwork persistentNetworkAB = new PersistentNetwork(new UUID(0, 0), networkStateAB);
    static Network networkAB = new Network(0, persistentNetworkAB);

    static Tree treeBA = new Tree(
        posB,
        Collections.singletonList(
            new Tree.Edge(new EdgeKey(posB, posA), 0, Tree.empty(posA, Measurements.UNIT_LENGTH))
        ),
        0, Measurements.UNIT_LENGTH * 2
    );
    static MutableSortedIntMap<ItemStack> attachmentsBA = MutableSortedIntMap.empty(Measurements.UNIT_LENGTH * 2);
    static NetworkState networkStateBA = new NetworkState(0, 0, 0, 0, treeBA, attachmentsBA);
    static PersistentNetwork persistentNetworkBA = new PersistentNetwork(new UUID(0, 0), networkStateBA);
    static Network networkBA = new Network(0, persistentNetworkBA);

    static void assertNetworksEquivalent(Network a, Network b) {
        Assertions.assertEquals(a.getId(), b.getId());
        Assertions.assertEquals(a.getUuid(), b.getUuid());
        assertNetworkStatesEquivalent(a.getState(), b.getState());
    }

    static void assertNetworkStatesEquivalent(NetworkState a, NetworkState b) {
        Assertions.assertEquals(a.getShift(), b.getShift());
        Assertions.assertEquals(a.getMomentum(), b.getMomentum());
        Assertions.assertEquals(a.getTree(), b.getTree());
        Assertions.assertEquals(a.getAttachments(), b.getAttachments());
    }
}
