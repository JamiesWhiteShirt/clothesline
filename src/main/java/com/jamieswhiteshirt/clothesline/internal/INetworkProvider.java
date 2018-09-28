package com.jamieswhiteshirt.clothesline.internal;

import com.jamieswhiteshirt.clothesline.api.PersistentNetwork;

import java.util.Collection;
import java.util.UUID;

public interface INetworkProvider {
    void reset(Collection<PersistentNetwork> persistentNetworks);

    Collection<PersistentNetwork> getNetworks();

    void addNetwork(PersistentNetwork persistentNetwork);

    void removeNetwork(UUID uuid);

    void onChunkLoaded(int x, int z);

    void onChunkUnloaded(int x, int z);
}
