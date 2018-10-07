package com.jamieswhiteshirt.clothesline.impl;

import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.api.AttachmentUnit;
import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.api.Tree;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkState;
import com.jamieswhiteshirt.clothesline.common.util.ChunkSpan;
import com.jamieswhiteshirt.clothesline.common.util.PathBuilder;
import it.unimi.dsi.fastutil.longs.LongSet;
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
                to.subtract(from),
                AttachmentUnit.lengthBetween(from, to),
                0,
                Tree.empty(to, AttachmentUnit.UNITS_PER_BLOCK, 0))
            ),
            0, AttachmentUnit.UNITS_PER_BLOCK * 2, 0
        );
        Path path = PathBuilder.buildPath(tree);
        LongSet chunkSpan = ChunkSpan.ofPath(path);
        MutableSortedIntMap<ItemStack> attachments = MutableSortedIntMap.empty(AttachmentUnit.UNITS_PER_BLOCK * 2);
        state = new NetworkState(0, 0, 0, 0, tree, path, chunkSpan, attachments);
    }

    void assertItemStacksEqual(ItemStack expected, ItemStack actual) {
        Assertions.assertTrue(ItemStack.areItemStacksEqual(expected, actual));
    }

    @Test
    void unsetItemsAreEmpty() {
        assertItemStacksEqual(state.getAttachment(0), ItemStack.EMPTY);
    }
}
