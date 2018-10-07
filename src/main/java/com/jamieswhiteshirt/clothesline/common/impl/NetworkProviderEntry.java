package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.internal.PersistentNetwork;
import it.unimi.dsi.fastutil.longs.LongSet;

public final class NetworkProviderEntry {
    private final PersistentNetwork persistentNetwork;
    private int loadCount;

    public NetworkProviderEntry(PersistentNetwork persistentNetwork) {
        this.persistentNetwork = persistentNetwork;
        this.loadCount = 0;
    }

    public PersistentNetwork getPersistentNetwork() {
        return persistentNetwork;
    }

    public boolean incrementLoadCount() {
        return loadCount++ == 0;
    }

    public boolean decrementLoadCount() {
        return --loadCount == 0;
    }
}
