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

    static AbsoluteTree absoluteTreeAB = new AbsoluteTree(
        posA,
        Collections.singletonList(
            new AbsoluteTree.Edge(new EdgeKey(posA, posB), 0, AbsoluteTree.empty(posB, Measurements.UNIT_LENGTH))
        ),
        0, Measurements.UNIT_LENGTH * 2
    );
    static MutableSortedIntMap<ItemStack> attachmentsAB = MutableSortedIntMap.empty(Measurements.UNIT_LENGTH * 2);
    static AbsoluteNetworkState absoluteNetworkStateAB = new AbsoluteNetworkState(0, 0, 0, 0, absoluteTreeAB, attachmentsAB);
    static PersistentNetwork persistentNetworkAB = new PersistentNetwork(new UUID(0, 0), absoluteNetworkStateAB);
    static Network networkAB = new Network(0, persistentNetworkAB);

    static AbsoluteTree absoluteTreeBA = new AbsoluteTree(
        posB,
        Collections.singletonList(
            new AbsoluteTree.Edge(new EdgeKey(posB, posA), 0, AbsoluteTree.empty(posA, Measurements.UNIT_LENGTH))
        ),
        0, Measurements.UNIT_LENGTH * 2
    );
    static MutableSortedIntMap<ItemStack> attachmentsBA = MutableSortedIntMap.empty(Measurements.UNIT_LENGTH * 2);
    static AbsoluteNetworkState absoluteNetworkStateBA = new AbsoluteNetworkState(0, 0, 0, 0, absoluteTreeBA, attachmentsBA);
    static PersistentNetwork persistentNetworkBA = new PersistentNetwork(new UUID(0, 0), absoluteNetworkStateBA);
    static Network networkBA = new Network(0, persistentNetworkBA);

    static void assertNetworksEquivalent(Network a, Network b) {
        Assertions.assertEquals(a.getId(), b.getId());
        Assertions.assertEquals(a.getUuid(), b.getUuid());
        assertNetworkStatesEquivalent(a.getState(), b.getState());
    }

    static void assertNetworkStatesEquivalent(AbsoluteNetworkState a, AbsoluteNetworkState b) {
        Assertions.assertEquals(a.getShift(), b.getShift());
        Assertions.assertEquals(a.getMomentum(), b.getMomentum());
        Assertions.assertEquals(a.getTree(), b.getTree());
        Assertions.assertEquals(a.getAttachments(), b.getAttachments());
    }
}
