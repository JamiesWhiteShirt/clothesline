package com.jamieswhiteshirt.clothesline.internal;

import net.minecraft.world.chunk.Chunk;

public interface INetworkCollectionTracker<T> {
    void onWatchChunk(T watcher, Chunk chunk);

    void onUnWatchChunk(T watcher, Chunk chunk);
}
