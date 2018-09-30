package com.jamieswhiteshirt.clothesline.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.INetworkCollection;
import com.jamieswhiteshirt.clothesline.internal.PersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.impl.Network;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkCollection;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkCollectionTracker;
import com.jamieswhiteshirt.clothesline.common.util.ISpanFunction;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import com.jamieswhiteshirt.clothesline.common.util.NodeSpanFunction;
import com.jamieswhiteshirt.clothesline.internal.INetworkCollectionTracker;
import com.jamieswhiteshirt.clothesline.internal.INetworkMessenger;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.UUID;
import java.util.function.BiFunction;

public class NetworkCollectionTrackerTest {
    INetworkCollection collection;
    INetworkMessenger<Object> messenger;
    SetMultimap<Long, Object> chunkWatchers;
    INetworkCollectionTracker<Object> tracker;
    Object watcher;

    INetwork network0 = createNetwork(0, new UUID(0, 0), new BlockPos(0, 0, 0), new BlockPos(16, 0, 0));
    long chunk0 = ISpanFunction.chunkPosition(0, 0);
    long chunk1 = ISpanFunction.chunkPosition(1, 0);

    INetwork createNetwork(int id, UUID uuid, BlockPos pos0, BlockPos pos1) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, pos0);
        stateBuilder.addEdge(pos0, pos1);
        return new Network(id, new PersistentNetwork(uuid, stateBuilder.build()));
    }

    void watchChunk(long position, Object watcher) {
        chunkWatchers.put(position, watcher);
        tracker.onWatchChunk(watcher, ISpanFunction.chunkX(position), ISpanFunction.chunkZ(position));
    }

    void unWatchChunk(long position, Object watcher) {
        chunkWatchers.remove(position, watcher);
        tracker.onUnWatchChunk(watcher, ISpanFunction.chunkX(position), ISpanFunction.chunkZ(position));
    }

    @BeforeEach
    void resetTracker() {
        collection = new NetworkCollection();
        messenger = Mockito.mock(INetworkMessenger.class);
        chunkWatchers = HashMultimap.create();
        BiFunction<Integer, Integer, Collection<Object>> getChunkWatchers = (Integer x, Integer z) -> chunkWatchers.get(ISpanFunction.chunkPosition(x, z));
        tracker = new NetworkCollectionTracker<>(collection, getChunkWatchers, messenger, new NodeSpanFunction());
        watcher = new Object();
    }

    @Test
    void sendsAddNetworkForFirstWatchedChunk() {
        collection.add(network0);

        Mockito.verifyNoMoreInteractions(messenger);

        watchChunk(chunk0, watcher);

        Mockito.verify(messenger).addNetwork(watcher, network0);

        watchChunk(chunk1, watcher);

        Mockito.verifyNoMoreInteractions(messenger);
    }

    @Test
    void sendsAddNetworkForExistingWatchedChunk() {
        watchChunk(chunk0, watcher);
        watchChunk(chunk1, watcher);

        Mockito.verifyNoMoreInteractions(messenger);

        collection.add(network0);

        Mockito.verify(messenger).addNetwork(watcher, network0);
        Mockito.verifyNoMoreInteractions(messenger);
    }

    @Test
    void sendsRemoveNetworkForLastUnWatchedChunk() {
        collection.add(network0);
        watchChunk(chunk0, watcher);

        Mockito.verify(messenger).addNetwork(watcher, network0);

        watchChunk(chunk1, watcher);
        unWatchChunk(chunk1, watcher);

        Mockito.verifyNoMoreInteractions(messenger);

        unWatchChunk(chunk0, watcher);

        Mockito.verify(messenger).removeNetwork(watcher, network0);
        Mockito.verifyNoMoreInteractions(messenger);
    }

    @Test
    void sendsRemoveNetworkForRemovedNetwork() {
        watchChunk(chunk0, watcher);
        collection.add(network0);

        Mockito.verify(messenger).addNetwork(watcher, network0);

        collection.remove(network0);

        Mockito.verify(messenger).removeNetwork(watcher, network0);
        Mockito.verifyNoMoreInteractions(messenger);
    }
}
