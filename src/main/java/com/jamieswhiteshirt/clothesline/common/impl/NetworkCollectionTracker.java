package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.util.ISpanFunction;
import com.jamieswhiteshirt.clothesline.internal.INetworkCollectionTracker;
import com.jamieswhiteshirt.clothesline.internal.INetworkMessenger;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;

import java.util.Collection;
import java.util.function.BiFunction;

public final class NetworkCollectionTracker<T> implements INetworkCollectionTracker<T> {
    private static final ResourceLocation LISTENER_KEY = new ResourceLocation("clothesline", "watcher");

    private final INetworkCollection networks;
    private final BiFunction<Integer, Integer, Collection<T>> getChunkWatchers;
    private final INetworkMessenger<T> messenger;
    private final ISpanFunction spanFunction;
    private final Int2ObjectMap<NetworkTracker<T>> networkTrackers = new Int2ObjectOpenHashMap<>();

    public NetworkCollectionTracker(INetworkCollection networks, BiFunction<Integer, Integer, Collection<T>> getChunkWatchers, INetworkMessenger<T> messenger, ISpanFunction spanFunction) {
        this.networks = networks;
        this.getChunkWatchers = getChunkWatchers;
        this.messenger = messenger;
        this.spanFunction = spanFunction;

        networks.addEventListener(LISTENER_KEY, new INetworkCollectionListener() {
            @Override
            public void onNetworkAdded(INetworkCollection networks, INetwork network) {
                addNetworkWatcher(network);
            }

            @Override
            public void onNetworkRemoved(INetworkCollection networks, INetwork network) {
                removeNetworkWatcher(network);
            }
        });
    }

    @Override
    public void onWatchChunk(T watcher, Chunk chunk) {
        for (INetwork network : spanFunction.getNetworkSpanOfChunk(networks, chunk.x, chunk.z)) {
            networkTrackers.get(network.getId()).addWatcher(watcher);
        }
    }

    @Override
    public void onUnWatchChunk(T watcher, Chunk chunk) {
        for (INetwork network : spanFunction.getNetworkSpanOfChunk(networks, chunk.x, chunk.z)) {
            networkTrackers.get(network.getId()).removeWatcher(watcher);
        }
    }

    private void addNetworkWatcher(INetwork network) {
        NetworkTracker<T> networkTracker = new NetworkTracker<>(network, messenger);
        network.addEventListener(LISTENER_KEY, networkTracker);
        networkTrackers.put(network.getId(), networkTracker);

        // Players may already be watching chunks that the network intersects with
        for (long position : spanFunction.getChunkSpanOfNetwork(network.getState())) {
            int x = ISpanFunction.chunkX(position);
            int z = ISpanFunction.chunkZ(position);
            for (T watcher : getChunkWatchers.apply(x, z)) {
                networkTracker.addWatcher(watcher);
            }
        }
    }

    private void removeNetworkWatcher(INetwork network) {
        network.removeEventListener(LISTENER_KEY);
        networkTrackers.remove(network.getId()).clear();
    }
}
