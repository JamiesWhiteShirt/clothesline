package com.jamieswhiteshirt.clothesline.impl;

import com.jamieswhiteshirt.clothesline.api.INetworkCollection;
import com.jamieswhiteshirt.clothesline.api.PersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkCollection;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkProvider;
import com.jamieswhiteshirt.clothesline.common.util.ISpanFunction;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import com.jamieswhiteshirt.clothesline.common.util.NodeSpanFunction;
import com.jamieswhiteshirt.clothesline.internal.INetworkProvider;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class NetworkProviderTest {
    ISpanFunction spanFunction = new NodeSpanFunction();
    PersistentNetwork network0 = createPersistentNetwork(new UUID(0, 0), new BlockPos(0, 0, 0), new BlockPos(16, 0, 0));
    long chunk0 = ISpanFunction.chunkPosition(0, 0);
    long chunk1 = ISpanFunction.chunkPosition(1, 0);

    INetworkProvider provider;
    INetworkCollection collection;
    LongSet loadedChunks;


    @BeforeEach
    void resetCollection() {
        collection = new NetworkCollection();
        loadedChunks = new LongOpenHashSet();
        provider = new NetworkProvider(collection, spanFunction, (Integer x, Integer z) -> loadedChunks.contains(ISpanFunction.chunkPosition(x, z)));
    }

    PersistentNetwork createPersistentNetwork(UUID uuid, BlockPos pos0, BlockPos pos1) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, pos0);
        stateBuilder.addEdge(pos0, pos1);
        return new PersistentNetwork(uuid, stateBuilder.build());
    }

    void loadChunk(long position) {
        loadedChunks.add(position);
        provider.onChunkLoaded(ISpanFunction.chunkX(position), ISpanFunction.chunkZ(position));
    }

    void unloadChunk(long position) {
        loadedChunks.remove(position);
        provider.onChunkUnloaded(ISpanFunction.chunkX(position), ISpanFunction.chunkZ(position));
    }

    @Test
    void loadsNetworkForFirstLoadedChunk() {
        provider.addNetwork(network0);

        Assertions.assertNull(collection.getByUuid(network0.getUuid()));

        loadChunk(chunk0);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));
    }

    @Test
    void loadsNetworkForExistingLoadedChunk() {
        loadChunk(chunk0);
        provider.addNetwork(network0);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));
    }

    @Test
    void unloadsNetworkForLastUnloadedChunk() {
        loadChunk(chunk0);
        loadChunk(chunk1);
        provider.addNetwork(network0);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));

        unloadChunk(chunk1);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));

        unloadChunk(chunk0);

        Assertions.assertNull(collection.getByUuid(network0.getUuid()));
    }

    @Test
    void unloadsNetworkWhenRemoved() {
        loadChunk(chunk0);
        provider.addNetwork(network0);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));

        provider.removeNetwork(network0.getUuid());

        Assertions.assertNull(collection.getByUuid(network0.getUuid()));
    }
}
