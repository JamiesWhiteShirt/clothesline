package com.jamieswhiteshirt.clothesline.impl;

import com.jamieswhiteshirt.clothesline.api.INetworkCollection;
import com.jamieswhiteshirt.clothesline.internal.PersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkCollection;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkProvider;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import com.jamieswhiteshirt.clothesline.internal.INetworkProvider;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class NetworkProviderTest {
    PersistentNetwork network0 = createPersistentNetwork(new UUID(0, 0), new BlockPos(0, 0, 0), new BlockPos(16, 0, 0));
    long chunk0 = ChunkPos.asLong(0, 0);
    long chunk1 = ChunkPos.asLong(1, 0);

    INetworkProvider provider;
    INetworkCollection collection;
    LongSet loadedChunks;


    @BeforeEach
    void resetCollection() {
        collection = new NetworkCollection();
        loadedChunks = new LongOpenHashSet();
        provider = new NetworkProvider(collection, (Integer x, Integer z) -> loadedChunks.contains(ChunkPos.asLong(x, z)));
    }

    PersistentNetwork createPersistentNetwork(UUID uuid, BlockPos pos0, BlockPos pos1) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, pos0);
        stateBuilder.addEdge(pos0, pos1);
        return new PersistentNetwork(uuid, stateBuilder.build());
    }

    void loadChunk(long position) {
        loadedChunks.add(position);
        provider.onChunkLoaded((int)position, (int)(position >> 32));
    }

    void unloadChunk(long position) {
        loadedChunks.remove(position);
        provider.onChunkUnloaded((int)position, (int)(position >> 32));
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
