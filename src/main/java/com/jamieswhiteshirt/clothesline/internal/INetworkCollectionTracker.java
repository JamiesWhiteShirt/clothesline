package com.jamieswhiteshirt.clothesline.internal;

public interface INetworkCollectionTracker<T> {
    void onWatchChunk(T watcher, int x, int z);

    void onUnWatchChunk(T watcher, int x, int z);

    void update();
}
