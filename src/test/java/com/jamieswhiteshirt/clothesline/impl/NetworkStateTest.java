package com.jamieswhiteshirt.clothesline.impl;

import com.jamieswhiteshirt.clothesline.api.DeltaKey;
import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.Tree;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkState;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class NetworkStateTest {
    @BeforeAll
    static void bootstrap() {
        Bootstrap.register();
    }

    INetworkState state;

    @BeforeEach
    void resetState() {
        BlockPos from = new BlockPos(0, 0, 0);
        BlockPos to = new BlockPos(1, 0, 0);
        Tree tree = new Tree(
            from,
            Collections.singletonList(new Tree.Edge(
                DeltaKey.between(from, to),
                Measurements.calculateDistance(from, to),
                0,
                Tree.empty(to, Measurements.UNIT_LENGTH, 0))
            ),
            0, Measurements.UNIT_LENGTH * 2, 0
        );
        MutableSortedIntMap<ItemStack> attachments = MutableSortedIntMap.empty(Measurements.UNIT_LENGTH * 2);
        state = new NetworkState(0, 0, 0, 0, tree, attachments);
    }

    void assertItemStacksEqual(ItemStack expected, ItemStack actual) {
        Assertions.assertTrue(ItemStack.areItemStacksEqual(expected, actual));
    }

    @Test
    void unsetItemsAreEmpty() {
        assertItemStacksEqual(state.getAttachment(0), ItemStack.EMPTY);
    }
}
