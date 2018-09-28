package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.PersistentNetwork;
import it.unimi.dsi.fastutil.longs.LongSet;

public final class NetworkProviderEntry {
    private final PersistentNetwork persistentNetwork;
    private final LongSet chunkSpan;
    private int loadCount;

    public NetworkProviderEntry(PersistentNetwork persistentNetwork, LongSet chunkSpan) {
        this.persistentNetwork = persistentNetwork;
        this.chunkSpan = chunkSpan;
        this.loadCount = 0;
    }

    public PersistentNetwork getPersistentNetwork() {
        return persistentNetwork;
    }

    public LongSet getChunkSpan() {
        return chunkSpan;
    }

    public boolean incrementLoadCount() {
        return loadCount++ == 0;
    }

    public boolean decrementLoadCount() {
        return --loadCount == 0;
    }
}
