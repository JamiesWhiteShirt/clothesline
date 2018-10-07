package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.internal.INetworkCollectionTracker;
import com.jamieswhiteshirt.clothesline.internal.INetworkMessenger;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.function.BiFunction;

public final class NetworkCollectionTracker<T> implements INetworkCollectionTracker<T> {
    private static final ResourceLocation LISTENER_KEY = new ResourceLocation("clothesline", "watcher");

    private final INetworkCollection networks;
    private final BiFunction<Integer, Integer, Collection<T>> getChunkWatchers;
    private final INetworkMessenger<T> messenger;
    private final Int2ObjectMap<NetworkTracker<T>> networkTrackers = new Int2ObjectOpenHashMap<>();

    public NetworkCollectionTracker(INetworkCollection networks, BiFunction<Integer, Integer, Collection<T>> getChunkWatchers, INetworkMessenger<T> messenger) {
        this.networks = networks;
        this.getChunkWatchers = getChunkWatchers;
        this.messenger = messenger;

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
    public void onWatchChunk(T watcher, int x, int z) {
        for (INetwork network : networks.getNetworksSpanningChunk(x, z)) {
            networkTrackers.get(network.getId()).addWatcher(watcher);
        }
    }

    @Override
    public void onUnWatchChunk(T watcher, int x, int z) {
        for (INetwork network : networks.getNetworksSpanningChunk(x, z)) {
            networkTrackers.get(network.getId()).removeWatcher(watcher);
        }
    }

    @Override
    public void update() {
        for (NetworkTracker<T> tracker : networkTrackers.values()) {
            tracker.update();
        }
    }

    private void addNetworkWatcher(INetwork network) {
        NetworkTracker<T> networkTracker = new NetworkTracker<>(network, messenger);
        network.addEventListener(LISTENER_KEY, networkTracker);
        networkTrackers.put(network.getId(), networkTracker);

        // Players may already be watching chunks that the network intersects with
        for (long position : network.getState().getChunkSpan()) {
            int x = (int)position;
            int z = (int)(position >> 32);
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
